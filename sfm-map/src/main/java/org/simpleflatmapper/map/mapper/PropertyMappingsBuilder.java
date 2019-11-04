package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.CaseInsensitiveFieldKeyNamePredicate;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.impl.ExtendPropertyFinder;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.map.property.OptionalProperty;
import org.simpleflatmapper.map.property.SetterProperty;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.map.PropertyNameMatcherFactory;
import org.simpleflatmapper.reflect.meta.PropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.SelfPropertyMeta;
import org.simpleflatmapper.reflect.property.EligibleAsNonMappedProperty;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.ErrorDoc;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public final class PropertyMappingsBuilder<T, K extends FieldKey<K>> {

	protected final PropertyFinder<T> propertyFinder;

	protected final List<PropertyMapping<T, ?, K>> properties = new ArrayList<PropertyMapping<T, ?, K>>();

	protected final PropertyNameMatcherFactory propertyNameMatcherFactory;

	private final MapperBuilderErrorHandler _mapperBuilderErrorHandler;
	private final ClassMeta<T> classMeta;
	private final PropertyMappingsBuilderProbe propertyMappingsBuilderProbe;
	private final PropertyPredicateFactory<K> isValidPropertyMeta;

	protected boolean modifiable = true;

	private List<ExtendPropertyFinder.CustomProperty<?, ?>> customProperties;

	private PropertyMappingsBuilder(final ClassMeta<T> classMeta,
									final PropertyNameMatcherFactory propertyNameMatcherFactory,
									final MapperBuilderErrorHandler mapperBuilderErrorHandler,
									final PropertyPredicateFactory<K> isValidPropertyMetaFactory,
									final PropertyFinder<T> propertyFinder,
									List<ExtendPropertyFinder.CustomProperty<?, ?>> customProperties,
									PropertyMappingsBuilderProbe propertyMappingsBuilderProbe)  throws MapperBuildingException {
		this.isValidPropertyMeta = isValidPropertyMetaFactory;
		this._mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		this.customProperties = customProperties;
		this.propertyMappingsBuilderProbe = propertyMappingsBuilderProbe;
		this.propertyFinder = propertyFinder != null ? propertyFinder : classMeta.newPropertyFinder();
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		this.classMeta = classMeta;
	}


	public <P> PropertyMapping<T, P, K> addProperty(final K key, final ColumnDefinition<K, ?> columnDefinition) {
		return
				_addProperty(key, columnDefinition, _mapperBuilderErrorHandler);
	}

	public <P> PropertyMapping<T, P, K> addPropertyIfPresent(final K key, final ColumnDefinition<K, ?> columnDefinition) {
		return _addProperty(key, columnDefinition, MapperBuilderErrorHandler.NULL);
	}

	@SuppressWarnings("unchecked")
	private <P> PropertyMapping<T, P, K> _addProperty(final K key, final ColumnDefinition<K, ?> columnDefinition, MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		if (!modifiable) throw new IllegalStateException("Builder not modifiable");

		if (columnDefinition.ignore()) {
			propertyMappingsBuilderProbe.ignore(key, columnDefinition);
			properties.add(null);
			return null;
		}

		PropertyFinder<T> effectivePropertyFinder = wrapPropertyFinder(this.propertyFinder);

		PropertyNameMatcher propertyNameMatcher = propertyNameMatcherFactory.newInstance(key);
		
		List<AccessorNotFound> accessorNotFounds = new ArrayList<AccessorNotFound>();

		PropertyFinder.PropertyFilter propertyFilter = isValidPropertyMeta.predicate(key, columnDefinition.properties(), accessorNotFounds);
		
		final PropertyMeta<T, P> prop =
				(PropertyMeta<T, P>) effectivePropertyFinder
						.findProperty(propertyNameMatcher, columnDefinition.properties(), toTypeAffinity(key), 
								propertyMappingsBuilderProbe.propertyFinderProbe(propertyNameMatcher),
								propertyFilter);


		if (prop == null) {
			if (!columnDefinition.has(OptionalProperty.class)) {
				AccessorNotFound accessorNotFound = selectAccessotNotFound(accessorNotFounds);
				if (accessorNotFound == null) {
					mapperBuilderErrorHandler.propertyNotFound(classMeta.getType(), key.getName());
				} else {
					mapperBuilderErrorHandler.accessorNotFound(accessorNotFounds.iterator().next().toString());
				}
			}
			properties.add(null);
			return null;
		} else {
			PropertyMapping<T, P, K> propertyMapping = addProperty(key, columnDefinition, prop);
			propertyMappingsBuilderProbe.map(key, columnDefinition, prop);

			handleSelfPropertyMetaInvalidation(mapperBuilderErrorHandler);

			return propertyMapping;
		}
	}

	private AccessorNotFound selectAccessotNotFound(List<AccessorNotFound> accessorNotFounds) {
		for(AccessorNotFound accessorNotFound : accessorNotFounds) {
			if (!accessorNotFound.propertyMeta.isSelf()) return accessorNotFound;
		}
		return null;
	}

	private TypeAffinity toTypeAffinity(K key) {
		if (key instanceof TypeAffinity) {
			return (TypeAffinity) key;
		}
		return null;
	}

	private <T> PropertyFinder<T> wrapPropertyFinder(PropertyFinder<T> propertyFinder) {

		if (!customProperties.isEmpty()) {
			return new ExtendPropertyFinder<T>(propertyFinder, customProperties, new Function<PropertyFinder.PropertyFinderTransformer, PropertyFinder.PropertyFinderTransformer>() {
				@Override
				public PropertyFinder.PropertyFinderTransformer apply(PropertyFinder.PropertyFinderTransformer propertyFinderTransformer) {
					return new ExtendPropertyFinder.ExtendPropertyFinderTransformer(propertyFinderTransformer, customProperties);
				}
			});
		}

		return propertyFinder;
	}

	private void handleSelfPropertyMetaInvalidation(MapperBuilderErrorHandler errorHandler) {
		List<K> invalidateKeys = new ArrayList<K>();

		for(ListIterator<PropertyMapping<T, ?, K>> iterator = properties.listIterator(); iterator.hasNext();) {
			PropertyMapping<T, ?, K> propertyMapping = iterator.next();
			if (propertyMapping != null && !propertyMapping.getPropertyMeta().isValid()) {
				iterator.set(null);
				if (!propertyMapping.getColumnDefinition().has(OptionalProperty.class)) {
					invalidateKeys.add(propertyMapping.getColumnKey());
				} else if (propertyMapping.getColumnDefinition().has(EligibleAsNonMappedProperty.class)) {
					iterator.set(propertyMapping.toNonMapped());
				}
			}
		}

		for(K k : invalidateKeys) {
			errorHandler.propertyNotFound(classMeta.getType(), k.getName());
		}
	}

	public <P> PropertyMapping<T, P, K> addProperty(final K key, final ColumnDefinition<K, ?> columnDefinition, final PropertyMeta<T, P> prop) {

//		for(PropertyMapping pm :properties) {
//			if (pm.getColumnKey().equals(key)) throw new IllegalStateException();
//		}

		if (columnDefinition.hasCustomSourceFrom(prop.getOwnerType())) {
			try {
				Type type = prop.getPropertyType();

				if (!checkTypeCompatibility(key, columnDefinition.getCustomSourceReturnTypeFrom(prop.getOwnerType()), type)) {
					properties.add(null);
					return null;
				}
			} catch (UnsupportedOperationException e) {
				// handle discriminator case that have different type...
				// assume the user is right
			}
		}

		Object[] definedProperties = prop.getDefinedProperties();
		ColumnDefinition<K, ?> mergeColumnDefinition = definedProperties != null ? columnDefinition.add(definedProperties) : columnDefinition;

		PropertyMapping<T, P, K> propertyMapping = new PropertyMapping<T, P, K>(prop, key, mergeColumnDefinition);

		properties.add(propertyMapping);
		
		propertyFinder.manualMatch(prop);

		return propertyMapping;
	}

	private boolean checkTypeCompatibility(K key, Type customSourceReturnType, Type propertyMetaType) {
		if (customSourceReturnType == null) {
			// cannot determine type
			return true;
		} else if(!areCompatible(propertyMetaType, customSourceReturnType)) {
			_mapperBuilderErrorHandler.customFieldError(key, "Incompatible customReader on '" + key.getName()+ "' type " + customSourceReturnType +  " expected " + propertyMetaType );
			return false;
		}
		return true;
	}

	private boolean areCompatible(Type propertyMetaType, Type customSourceReturnType) {
		Class<?> propertyMetaClass = TypeHelper.toBoxedClass(TypeHelper.toClass(propertyMetaType));
		Class<?> customSourceReturnClass = TypeHelper.toBoxedClass(TypeHelper.toClass(customSourceReturnType));
		return propertyMetaClass.isAssignableFrom(customSourceReturnClass);
	}


	public List<K> getKeys() {
		modifiable = false;

		List<K>  keys = new ArrayList<K>(properties.size());
		for (PropertyMapping<T, ?, K> propMapping : properties) {
			if (propMapping != null) {
				keys.add(propMapping.getColumnKey());
			} else {
				keys.add(null);
			}

		}
		return keys;
	}

	public void forEachConstructorProperties(ForEachCallBack<PropertyMapping<T, ?, K>> handler)  {
		modifiable = false;

		for (PropertyMapping<T, ?, K> property : properties) {
			if (property != null) {
				PropertyMeta<T, ?> propertyMeta = property.getPropertyMeta();
				if (propertyMeta != null && propertyMeta.isConstructorProperty() && ! propertyMeta.isSubProperty()) {
					handler.handle(property);
				}
			}
		}
	}

	public List<PropertyMapping<T, ?, K>> currentProperties() {
		return new ArrayList<PropertyMapping<T, ?, K>>(properties);
	}

	public <H extends ForEachCallBack<PropertyMapping<T, ?, K>>> H forEachProperties(H handler)  {
		return forEachProperties(handler, -1 );
	}

	public <F extends ForEachCallBack<PropertyMapping<T, ?, K>>> F forEachProperties(F handler, int start)  {
		return forEachProperties(handler, start, -1 );
	}

	public <F extends ForEachCallBack<PropertyMapping<T, ?, K>>> F forEachProperties(F handler, int start, int end)  {
		modifiable = false;
		for (PropertyMapping<T, ?, K> prop : properties) {
			if (prop != null
					&& (prop.getColumnKey().getIndex() >= start || start == -1)
					&& (prop.getColumnKey().getIndex() < end || end == -1)) {
				handler.handle(prop);
			}
		}
		return handler;
	}


	public PropertyFinder<T> getPropertyFinder() {
		modifiable = false;
		return propertyFinder;
	}


	public int size() {
		return properties.size();
	}


	public boolean isSelfProperty() {
		return  (properties.size() == 1 && properties.get(0) != null && properties.get(0).getPropertyMeta() instanceof SelfPropertyMeta);
	}

	public int maxIndex() {
		int i = -1;
		for (PropertyMapping<T, ?, K> prop : properties) {
			if (prop != null) {
				i = Math.max(i, prop.getColumnKey().getIndex());
			}
		}
		return i;
	}

	public boolean hasKey(Predicate<? super K> predicate) {
		for (PropertyMapping<T, ?, K> propMapping : properties) {
			if (propMapping != null && predicate.test(propMapping.getColumnKey())) {
				return true;
			}
		}

		return false;
	}

	public ClassMeta<T> getClassMeta() {
		return classMeta;
	}

	public static <T, K extends FieldKey<K>, S> PropertyMappingsBuilder<T, K> of(
			ClassMeta<T> classMeta,
			MapperConfig<K, S> mapperConfig,
			PropertyPredicateFactory<K> propertyPredicateFactory) {
		return of(classMeta, mapperConfig, propertyPredicateFactory, null);
	}

	public static <T, K extends FieldKey<K>, S> PropertyMappingsBuilder<T, K> of(
			final ClassMeta<T> classMeta,
			final MapperConfig<K, S> mapperConfig,
			final PropertyPredicateFactory<K> propertyPredicateFactory,
			final PropertyFinder<T> propertyFinder) {
		final List<ExtendPropertyFinder.CustomProperty<?, ?>> customProperties = new ArrayList<ExtendPropertyFinder.CustomProperty<?, ?>>();

		final PropertyFinder.PropertyFilter propertyPredicate = propertyPredicateFactory.predicate(null, null, new ArrayList<AccessorNotFound>());
		// setter
		mapperConfig.columnDefinitions().forEach(SetterProperty.class, new BiConsumer<Predicate<? super K>, SetterProperty>() {
			@Override
			public void accept(Predicate<? super K> predicate, SetterProperty setterProperty) {
				if (predicate instanceof CaseInsensitiveFieldKeyNamePredicate) {
					CaseInsensitiveFieldKeyNamePredicate p = (CaseInsensitiveFieldKeyNamePredicate) predicate;
					ExtendPropertyFinder.CustomProperty cp = new ExtendPropertyFinder.CustomProperty(setterProperty.getTargetType(), classMeta.getReflectionService(), p.getName(), setterProperty.getPropertyType(), setterProperty.getSetter(), NullGetter.getter());
					if (propertyPredicate.testProperty(cp)) {
						customProperties.add(cp);
					}
				}
			}
		});

		// getter
		mapperConfig.columnDefinitions().forEach(GetterProperty.class, new BiConsumer<Predicate<? super K>, GetterProperty>() {
			@Override
			public void accept(Predicate<? super K> predicate, GetterProperty getterProperty) {
				if (predicate instanceof CaseInsensitiveFieldKeyNamePredicate) {
					CaseInsensitiveFieldKeyNamePredicate p = (CaseInsensitiveFieldKeyNamePredicate) predicate;
					ExtendPropertyFinder.CustomProperty cp = new ExtendPropertyFinder.CustomProperty(getterProperty.getSourceType(), classMeta.getReflectionService(), p.getName(), getterProperty.getReturnType(), NullSetter.NULL_SETTER, getterProperty.getGetter());
					if (propertyPredicate.testProperty(cp)) {
						customProperties.add(cp);
					}
				}
			}
		});

		return
				new PropertyMappingsBuilder<T, K>(
						classMeta,
						mapperConfig.propertyNameMatcherFactory(),
						mapperConfig.mapperBuilderErrorHandler(),
						propertyPredicateFactory,
						propertyFinder,
						customProperties, 
						DefaultPropertyMappingsBuilderProbe.INSTANCE);
	}

	public interface PropertyMappingsBuilderProbe {
		void ignore(FieldKey key, ColumnDefinition columnDefinition);

		void map(FieldKey key, ColumnDefinition columnDefinition, PropertyMeta<?, ?> prop);

		PropertyFinder.PropertyFinderProbe propertyFinderProbe(PropertyNameMatcher matcher);
	}
	
	
	private static class DefaultPropertyMappingsBuilderProbe implements PropertyMappingsBuilderProbe {
		static final DefaultPropertyMappingsBuilderProbe INSTANCE = new DefaultPropertyMappingsBuilderProbe();
		private static final boolean DEBUG = Boolean.getBoolean("org.simpleflatmapper.probe.propertyMappingsBuilder");

		@Override
		public void ignore(FieldKey key, ColumnDefinition columnDefinition) {
			if (DEBUG) {
				System.out.println("PropertyMappingsBuilder - ignore " + key);
			}
		}

		@Override
		public void map(FieldKey key, ColumnDefinition columnDefinition, PropertyMeta<?, ?> prop) {
			if (DEBUG) {
				String path = prop.getPath();
				System.out.println("PropertyMappingsBuilder - map " + key.getName() + " to " + path);
			}
			
		}

		@Override
		public PropertyFinder.PropertyFinderProbe propertyFinderProbe(PropertyNameMatcher matcher) {
			return new PropertyFinder.DefaultPropertyFinderProbe(matcher);
		}
	}

	public interface PropertyPredicateFactory<K extends FieldKey<K>> {
		PropertyFinder.PropertyFilter predicate(K key, Object[] properties, List<AccessorNotFound> accessorNotFounds);
	}
	
	
	public static final class AccessorNotFound {
		
		public final FieldKey<?> key;
		public final String path;
		public final Type to;
		public final ErrorDoc errorDoc;
		public final PropertyMeta<?, ?> propertyMeta;

		public AccessorNotFound(FieldKey<?> key, String path, Type to, ErrorDoc errorDoc, PropertyMeta<?, ?> propertyMeta) {
			this.key = key;
			this.path = path;
			this.to = to;
			this.errorDoc = errorDoc;
			this.propertyMeta = propertyMeta;
		}


		public String toString() {
			String accessor = "NA";
			
			switch (errorDoc) {
				case CSFM_GETTER_NOT_FOUND: accessor = "Getter"; break;
				case PROPERTY_NOT_FOUND: accessor = "property"; break;
				case CTFM_SETTER_NOT_FOUND: accessor = "Setter"; break;
				case CTFM_GETTER_NOT_FOUND: accessor = "Getter"; break;
			}
			
			return "Could not find " + accessor + " for " + key + " returning type "
					+ to 
					+ " path " + path
					+ ". See " + errorDoc.toUrl();
		}
	}
}
