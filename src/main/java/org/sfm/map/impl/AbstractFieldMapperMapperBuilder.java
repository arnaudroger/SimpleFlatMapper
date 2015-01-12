package org.sfm.map.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.getter.MapperGetterAdapter;
import org.sfm.map.*;
import org.sfm.map.impl.fieldmapper.FieldMapperImpl;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.ConstructorParameter;
import org.sfm.reflect.meta.*;
import org.sfm.utils.ForEachCallBack;

public abstract class AbstractFieldMapperMapperBuilder<S, T, K extends FieldKey<K>>  {

	private final Type source;
	protected final Type target;

	private final FieldMapperFactory<S, K> fieldMapperFactory;
	private final GetterFactory<S, K> getterFactory;

	private final PropertyMappingsBuilder<T, K,FieldMapperColumnDefinition<K>> propertyMappingsBuilder;
	protected final ReflectionService reflectionService;
	
	protected final Map<String, FieldMapperColumnDefinition<K>> columnDefinitions;
	protected final List<FieldMapper<S, T>> additionalMappers = new ArrayList<>();
	protected final PropertyNameMatcherFactory propertyNameMatcherFactory;

	private final FieldMapperColumnDefinition<K> identity = FieldMapperColumnDefinition.identity();

	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	private FieldMapperErrorHandler<K> fieldMapperErrorHandler;

	public AbstractFieldMapperMapperBuilder(final Type target, final Type source, final ClassMeta<T> classMeta,   
			GetterFactory<S, K> getterFactory, FieldMapperFactory<S, K> fieldMapperFactory, 
			Map<String, FieldMapperColumnDefinition<K>> columnDefinitions, PropertyNameMatcherFactory propertyNameMatcherFactory
			) throws MapperBuildingException {
		if (target == null) {
			throw new NullPointerException("target is null");
		}
		if (source == null) {
			throw new NullPointerException("source is null");
		}
		if (classMeta == null) {
			throw new NullPointerException("classMeta is null");
		}
		if (getterFactory == null) {
			throw new NullPointerException("getterFactory is null");
		}
		if (fieldMapperFactory == null) {
			throw new NullPointerException("fieldMapperFactory is null");
		}
		if (columnDefinitions == null) {
			throw new NullPointerException("columnDefinitions is null");
		}
		if (propertyNameMatcherFactory == null) {
			throw new NullPointerException("propertyNameMatcherFactory is null");
		}
		this.source = source;
		this.getterFactory = getterFactory;
		this.fieldMapperFactory = fieldMapperFactory;
		this.propertyMappingsBuilder = new PropertyMappingsBuilder<T, K, FieldMapperColumnDefinition<K>>(classMeta, propertyNameMatcherFactory);
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		this.target = target;
		this.reflectionService = classMeta.getReflectionService();
		this.columnDefinitions = columnDefinitions;
	}

	protected Class<T> getTargetClass() {
		return TypeHelper.toClass(target);
	}

	@SuppressWarnings("unchecked")
	protected Instantiator<S, T> getInstantiator() throws MapperBuildingException {
		InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();
		try {
			return instantiatorFactory.getInstantiator(source, target, propertyMappingsBuilder, constructorInjections());
		} catch(Exception e) {
			throw new MapperBuildingException(e.getMessage(), e);
		}
	}

	private Map<ConstructorParameter, Getter<S, ?>> constructorInjections() {
		final Map<ConstructorParameter, Getter<S, ?>> injections = new HashMap<ConstructorParameter, Getter<S, ?>>();
		
		propertyMappingsBuilder.forEachConstructorProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K>>>() {
			@Override
			public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> t) {
				PropertyMeta<T, ?> pm  = t.getPropertyMeta();
					ConstructorPropertyMeta<T, ?> cProp = (ConstructorPropertyMeta<T, ?>) pm;
					ConstructorParameter constructorParameter = cProp.getConstructorParameter();
					injections.put(constructorParameter, getterFor(t.getColumnKey(), constructorParameter.getResolvedType()));
			}
		});
		
		final Map<ConstructorParameter, AbstractFieldMapperMapperBuilder<S, ?, K>> builderToInject = new HashMap<ConstructorParameter, AbstractFieldMapperMapperBuilder<S, ?, K>>();
		propertyMappingsBuilder.forEachSubProperties(new ForEachCallBack<PropertyMapping<T,?, K, FieldMapperColumnDefinition<K>>>() {
			@Override
			public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> t) {
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
			private <P> void addPropertyoBuilder(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> t,
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
	protected void addMapping(K key, final FieldMapperColumnDefinition<K> columnDefinition) {
			final FieldMapperColumnDefinition<K> composedDefinition = FieldMapperColumnDefinition.compose(columnDefinition, getColumnDefintion(key));
			final K mappedColumnKey = composedDefinition.rename(key);

		if (!propertyMappingsBuilder.addProperty(mappedColumnKey, composedDefinition)) {
			mapperBuilderErrorHandler.propertyNotFound(target, key.getName());
		}
	}


	private FieldMapperColumnDefinition<K> getColumnDefintion(K key) {
		FieldMapperColumnDefinition<K> definition = columnDefinitions.get(key.getName().toLowerCase());

		if (definition == null) {
			return FieldMapperColumnDefinition.identity();
		} else {
			return definition;
		}
	}
	
	protected <P> void addMapping(K columnKey, PropertyMeta<T, P> prop) {
		propertyMappingsBuilder.addProperty(columnKey, identity, prop);
	}
	private <P> Getter<S, P> newSubMapperGetter(AbstractFieldMapperMapperBuilder<S, P, K> value) {
		return new MapperGetterAdapter<S, P>(value.mapper());
	}

	@SuppressWarnings("unchecked")
	public final FieldMapper<S, T>[] fields() {
		final List<FieldMapper<S, T>> fields = new ArrayList<FieldMapper<S, T>>();
		
		final Map<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>, AbstractFieldMapperMapperBuilder<S, ?, K>> buildersByOwner =
				new HashMap<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>, AbstractFieldMapperMapperBuilder<S,?,K>>();
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K>>>() {
			
			final Map<String, AbstractFieldMapperMapperBuilder<S, ?, K>> builders = new HashMap<String, AbstractFieldMapperMapperBuilder<S,?,K>>();
			@Override
			public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> t) {
				if (t == null) return;
				PropertyMeta<T, ?> meta = t.getPropertyMeta();
				if (meta == null) return;
				if (meta.isSubProperty()) {
					addSubProperty(t,  (SubPropertyMeta<T, ?>) meta, t.getColumnKey());
				} else if (!meta.isConstructorProperty()) {
					fields.add(newFieldMapper(t));
				}
			}
			private <P> void addSubProperty(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> pm,  SubPropertyMeta<T, ?> subPropertyMeta, K key) {
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
		
		for(Entry<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>, AbstractFieldMapperMapperBuilder<S, ?, K>> e : buildersByOwner.entrySet()) {
			SubPropertyMeta<T, ?> prop = (SubPropertyMeta<T, ?>) e.getKey().getPropertyMeta();
			fields.add(newSubFieldMapper(prop.getOwnerProperty(), e.getValue(), e.getKey().getColumnKey()));
		}

		for(FieldMapper<S, T> mapper : additionalMappers) {
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

	protected <P> FieldMapper<S, T> newFieldMapper(PropertyMapping<T, P, K, FieldMapperColumnDefinition<K>> t) {
		FieldMapper<S, T> fieldMapper = (FieldMapper<S, T>) t.getColumnDefinition().getFieldMapper();

		if (fieldMapper == null) {
			fieldMapper = fieldMapperFactory.newFieldMapper(t.getPropertyMeta().getType(), t.getPropertyMeta().getSetter(), t.getColumnKey(), fieldMapperErrorHandler, mapperBuilderErrorHandler);
		}

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


	protected void _addMapper(FieldMapper<S, T> mapper) {
		additionalMappers.add(mapper);
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