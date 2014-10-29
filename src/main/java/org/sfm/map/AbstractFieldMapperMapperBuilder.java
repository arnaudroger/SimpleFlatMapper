package org.sfm.map;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sfm.csv.MappingKey;
import org.sfm.jdbc.MapperGetterAdapter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.asm.ConstructorParameter;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.ConstructorPropertyMeta;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.meta.SubPropertyMeta;
import org.sfm.utils.ForEachCallBack;

public abstract class AbstractFieldMapperMapperBuilder<S, T, K extends MappingKey<K>>  {

	private final Type source;
	protected final Type target;

	private final FieldMapperFactory<S, K> fieldMapperFactory;
	private final GetterFactory<S, K> getterFactory;

	private final PropertyMappingsBuilder<T, K> propertyMappingsBuilder;
	protected final ReflectionService reflectionService;
	
	protected final Map<String, String> aliases;
	protected final Map<String, FieldMapper<S, ?>> customMappings;
	protected final List<KeyFieldMapperCouple<S, T, K>> mappers;

	
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	private FieldMapperErrorHandler<K> fieldMapperErrorHandler;
	
	public AbstractFieldMapperMapperBuilder(final Type target, final Type source, final ClassMeta<T> classMeta,   
			GetterFactory<S, K> getterFactory, FieldMapperFactory<S, K> fieldMapperFactory, 
			Map<String, String> aliases, Map<String, FieldMapper<S, ?>> customMappings
			) throws MapperBuildingException {
		this.source = source;
		this.getterFactory = getterFactory;
		this.fieldMapperFactory = fieldMapperFactory;
		this.propertyMappingsBuilder = new PropertyMappingsBuilder<T, K>(classMeta);
		this.target = target;
		this.reflectionService = classMeta.getReflectionService();
		this.aliases = aliases;
		this.customMappings = customMappings;
		this.mappers = new ArrayList<KeyFieldMapperCouple<S,T,K>>();
	}

	protected Class<T> getTargetClass() {
		return TypeHelper.toClass(target);
	}

	@SuppressWarnings("unchecked")
	protected Instantiator<S, T> getInstantiator() throws MapperBuildingException {
		InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();
		if (!reflectionService.isAsmPresent()) {
			try {
				return (Instantiator<S, T>) instantiatorFactory.getInstantiator(TypeHelper.toClass(source), propertyMappingsBuilder.getPropertyFinder().getClassToInstantiate());
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		} else {
			try {
				return instantiatorFactory.getInstantiator(TypeHelper.toClass(source), propertyMappingsBuilder.getPropertyFinder().getEligibleConstructorDefinitions(), constructorInjections());
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		}
	}

	private Map<ConstructorParameter, Getter<S, ?>> constructorInjections() {
		final Map<ConstructorParameter, Getter<S, ?>> injections = new HashMap<ConstructorParameter, Getter<S, ?>>();
		
		propertyMappingsBuilder.forEachConstructorProperties(new ForEachCallBack<PropertyMapping<T,?,K>>() {
			@Override
			public void handle(PropertyMapping<T, ?, K> t, int index) {
				PropertyMeta<T, ?> pm  = t.getPropertyMeta();
					ConstructorPropertyMeta<T, ?> cProp = (ConstructorPropertyMeta<T, ?>) pm;
					ConstructorParameter constructorParameter = cProp.getConstructorParameter();
					injections.put(constructorParameter, getterFor(t.getColumnKey(), constructorParameter.getType()));
			}
		});
		
		final Map<ConstructorParameter, AbstractFieldMapperMapperBuilder<S, ?, K>> builderToInject = new HashMap<ConstructorParameter, AbstractFieldMapperMapperBuilder<S, ?, K>>();
		propertyMappingsBuilder.forEachSubProperties(new ForEachCallBack<PropertyMapping<T,?,K>>() {
			@Override
			public void handle(PropertyMapping<T, ?, K> t, int index) {
				PropertyMeta<T, ?> pm  = t.getPropertyMeta();
				SubPropertyMeta<T, ?> subProp = (SubPropertyMeta<T, ?>) pm;
				PropertyMeta<T, ?> propOwner = subProp.getOwnerProperty();
				if (propOwner.isConstructorProperty()) {
					ConstructorParameter param = ((ConstructorPropertyMeta<T, ?>)propOwner).getConstructorParameter();
					AbstractFieldMapperMapperBuilder<S, ?, K> builder = builderToInject.get(param);
					if (builder == null) {
						builder = newSubBuilder(propOwner.getType(), propOwner.getClassMeta());
						builderToInject.put(param, builder);
					}
					addPropertyoBuilder(t, subProp, builder);
				}
			}

			@SuppressWarnings("unchecked")
			private <P> void addPropertyoBuilder(PropertyMapping<T, ?, K> t,
					SubPropertyMeta<T, ?> subProp,
					AbstractFieldMapperMapperBuilder<S, ?, K> builder) {
				((AbstractFieldMapperMapperBuilder<S, P, K>)builder).addMapping(t.getColumnKey(), ((SubPropertyMeta<T, P>)subProp).getSubProperty());
			}
		});
		
		for(Entry<ConstructorParameter, AbstractFieldMapperMapperBuilder<S, ?, K>> e : builderToInject.entrySet()) {
			injections.put(e.getKey(), newSubMapperGetter(e.getValue()));
		}
		
		return injections;
	}

	@SuppressWarnings("unchecked")
	protected void addMapping(K columnKey) {
		if (customMappings != null && customMappings.containsKey(columnKey.getName().toUpperCase())) {
			mappers.add(
					new KeyFieldMapperCouple<S, T, K>(columnKey, 
						(FieldMapper<S, T>) customMappings.get(columnKey.getName().toUpperCase())));
		} else {
			K alias = alias(columnKey);
			if (! propertyMappingsBuilder.addProperty(alias)) {
				mapperBuilderErrorHandler.propertyNotFound(target, columnKey.getName());
			}
		}
	}
	
	private K alias(K key) {
		if (aliases == null || aliases.isEmpty()) {
			return key;
		} 
		String alias = aliases.get(key.getName().toUpperCase());
		if (alias == null) {
			return key;
		}
		return key.alias(alias);
	}
	
	protected <P> void addMapping(K columnKey, PropertyMeta<T, P> prop) {
		propertyMappingsBuilder.addProperty(columnKey, prop);
	}
	private <P> Getter<S, P> newSubMapperGetter(AbstractFieldMapperMapperBuilder<S, P, K> value) {
		return new MapperGetterAdapter<S, P>(value.mapper());
	}

	@SuppressWarnings("unchecked")
	public final FieldMapper<S, T>[] fields() {
		final List<FieldMapper<S, T>> fields = new ArrayList<FieldMapper<S, T>>();
		
		final Map<PropertyMapping<T, ?, K>, AbstractFieldMapperMapperBuilder<S, ?, K>> buildersByOwner = 
				new HashMap<PropertyMapping<T, ?, K>, AbstractFieldMapperMapperBuilder<S,?,K>>();
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,K>>() {
			
			final Map<String, AbstractFieldMapperMapperBuilder<S, ?, K>> builders = new HashMap<String, AbstractFieldMapperMapperBuilder<S,?,K>>();
			@Override
			public void handle(PropertyMapping<T, ?, K> t, int index) {
				if (t == null) return;
				PropertyMeta<T, ?> meta = t.getPropertyMeta();
				if (meta == null) return;
				if (meta.isSubProperty()) {
					addSubProperty(t,  (SubPropertyMeta<T, ?>) meta, t.getColumnKey());
				} else if (!meta.isConstructorProperty()) {
					fields.add(newFieldMapper(t));
				}
			}
			private <P> void addSubProperty(PropertyMapping<T, ?, K> pm,  SubPropertyMeta<T, ?> subPropertyMeta, K key) {
				PropertyMeta<T, ?> propertyOwner = subPropertyMeta.getOwnerProperty();
				if (!propertyOwner.isConstructorProperty()) {
					AbstractFieldMapperMapperBuilder<S, P, K> builder = (AbstractFieldMapperMapperBuilder<S, P, K>) builders.get(propertyOwner.getName());
					if (builder == null) {
						builder = (AbstractFieldMapperMapperBuilder<S, P, K>) newSubBuilder(propertyOwner.getType(), propertyOwner.getClassMeta());
						builders.put(propertyOwner.getName(), builder);
						buildersByOwner.put(pm, builder);
					}
					builder.addMapping(key, ((SubPropertyMeta<T, P>)subPropertyMeta).getSubProperty());
				}
			}
		});
		
		for(Entry<PropertyMapping<T, ?, K>, AbstractFieldMapperMapperBuilder<S, ?, K>> e : buildersByOwner.entrySet()) {
			SubPropertyMeta<T, ?> prop = (SubPropertyMeta<T, ?>) e.getKey().getPropertyMeta();
			fields.add(newSubFieldMapper(prop.getOwnerProperty(), e.getValue(), e.getKey().getColumnKey()));
		}
		
		for(KeyFieldMapperCouple<S, T, K> keyFieldMapper: mappers) {
			FieldMapper<S, T> mapper = keyFieldMapper.getFieldMapper();
			if (fieldMapperErrorHandler != null) {
				mapper = new FieldErrorHandlerMapper<S, T, K>(keyFieldMapper.getKey(), mapper, fieldMapperErrorHandler);
			}
			fields.add(mapper);
		}
		
		return fields.toArray(new FieldMapper[fields.size()]);
	}


	@SuppressWarnings("unchecked")
	private <P> FieldMapper<S, T> newSubFieldMapper(PropertyMeta<T, ?> prop,
			AbstractFieldMapperMapperBuilder<S, ?, K> builder, K key) {
		Setter<T, P> setter = (Setter<T, P>) prop.getSetter();
		return newFieldMapper(builder, setter, key);		
	}

	@SuppressWarnings("unchecked")
	private <P> FieldMapper<S, T> newFieldMapper(
			AbstractFieldMapperMapperBuilder<S, ?, K> builder,
			Setter<T, P> setter, K key) {
		FieldMapper<S, T> fm =  new FieldMapperImpl<S, T, P>((Getter<S, ? extends P>) newSubMapperGetter(builder), setter);
		if (fieldMapperErrorHandler != null) {
			fm = new FieldErrorHandlerMapper<S, T, K>(key, fm, fieldMapperErrorHandler);
		}
		return fm;
	}

	protected <P> FieldMapper<S, T> newFieldMapper(PropertyMapping<T, P, K> t) {
		FieldMapper<S, T> fieldMapper = fieldMapperFactory.newFieldMapper(t.getPropertyMeta().getSetter(), t.getColumnKey(), fieldMapperErrorHandler, mapperBuilderErrorHandler);
		if (fieldMapperErrorHandler != null) {
			fieldMapper = new FieldErrorHandlerMapper<S, T, K>(t.getColumnKey(), fieldMapper, fieldMapperErrorHandler);
		}
		return fieldMapper;
	}

	private Getter<S, Object> getterFor(K key, Type paramType) {
		Getter<S, Object> getter = getterFactory.newGetter(paramType, key);
		if (getter == null) {
			mapperBuilderErrorHandler.getterNotFound("Could not find getter for " + key + " type " + paramType);
		}
		return getter;
	}

	protected abstract <ST> AbstractFieldMapperMapperBuilder<S, ST, K> newSubBuilder(Type type, ClassMeta<ST> classMeta);
	
	public abstract Mapper<S, T> mapper();

	protected void setMapperBuilderErrorHandler(MapperBuilderErrorHandler errorHandler) {
		this.mapperBuilderErrorHandler = errorHandler;
	}
	
	protected void setFieldMapperErrorHandler(
			FieldMapperErrorHandler<K> errorHandler) {
		this.fieldMapperErrorHandler = errorHandler;
	}
}