package org.sfm.map.impl;

import org.sfm.jdbc.impl.getter.MapperGetterAdapter;
import org.sfm.map.*;
import org.sfm.map.impl.fieldmapper.MapperFieldMapper;
import org.sfm.reflect.*;
import org.sfm.reflect.meta.*;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ForEachCallBack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractFieldMapperMapperBuilder<S, T, K extends FieldKey<K>>  {

	private final Type source;
	private final Type target;

	private final FieldMapperFactory<S, K, FieldMapperColumnDefinition<K, S>> fieldMapperFactory;
	private final GetterFactory<S, K> getterFactory;

	private final PropertyMappingsBuilder<T, K,FieldMapperColumnDefinition<K, S>> propertyMappingsBuilder;
	protected final ReflectionService reflectionService;
	
	protected final ColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> columnDefinitions;
	private final List<FieldMapper<S, T>> additionalMappers = new ArrayList<FieldMapper<S, T>>();
	protected final PropertyNameMatcherFactory propertyNameMatcherFactory;

	private final FieldMapperColumnDefinition<K, S> identity = FieldMapperColumnDefinition.identity();

	protected final MapperBuilderErrorHandler mapperBuilderErrorHandler;
	private FieldMapperErrorHandler<K> fieldMapperErrorHandler;

	public AbstractFieldMapperMapperBuilder(final Type source,
											final ClassMeta<T> classMeta,
											GetterFactory<S, K> getterFactory,
											FieldMapperFactory<S, K, FieldMapperColumnDefinition<K, S>> fieldMapperFactory,
											ColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> columnDefinitions,
											PropertyNameMatcherFactory propertyNameMatcherFactory,
											MapperBuilderErrorHandler mapperBuilderErrorHandler) throws MapperBuildingException {
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
		if (mapperBuilderErrorHandler == null) {
			throw new NullPointerException("mapperBuilderErrorHandler is null");
		}
		this.source = source;
		this.getterFactory = getterFactory;
		this.fieldMapperFactory = fieldMapperFactory;
		this.propertyMappingsBuilder = new PropertyMappingsBuilder<T, K, FieldMapperColumnDefinition<K, S>>(classMeta, propertyNameMatcherFactory, mapperBuilderErrorHandler);
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		this.target = classMeta.getType();
		this.reflectionService = classMeta.getReflectionService();
		this.columnDefinitions = columnDefinitions;
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;

	}

	protected Class<T> getTargetClass() {
		return TypeHelper.toClass(target);
	}

	@SuppressWarnings("unchecked")
	protected Tuple2<FieldMapper<S,T>[], Instantiator<S, T>> getConstructorFieldMappersAndInstantiator() throws MapperBuildingException {

		InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();
		try {
            Tuple2<Map<ConstructorParameter, Getter<S, ?>>, FieldMapper<S, T>[]> constructorInjections = constructorInjections();
            Map<ConstructorParameter, Getter<S, ?>> injections = constructorInjections.first();
            Instantiator<S, T> instantiator = instantiatorFactory.getInstantiator(source, target, propertyMappingsBuilder, injections, getterFactory);
            return new Tuple2<FieldMapper<S, T>[], Instantiator<S, T>>(constructorInjections.second(), instantiator);
		} catch(Exception e) {
			throw new MapperBuildingException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
    private Tuple2<Map<ConstructorParameter, Getter<S, ?>>, FieldMapper<S, T>[]> constructorInjections() {
		final Map<ConstructorParameter, Getter<S, ?>> injections = new HashMap<ConstructorParameter, Getter<S, ?>>();
		final List<FieldMapper<S, T>> fieldMappers = new ArrayList<FieldMapper<S, T>>();
		propertyMappingsBuilder.forEachConstructorProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K, S>>>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> t) {
				PropertyMeta<T, ?> pm  = t.getPropertyMeta();
					ConstructorPropertyMeta<T, ?> cProp = (ConstructorPropertyMeta<T, ?>) pm;
					ConstructorParameter constructorParameter = cProp.getConstructorParameter();
					injections.put(constructorParameter, getterFor(t, constructorParameter.getResolvedType()));
			}
		});
		
		final Map<ConstructorPropertyMeta<T, ?>, AbstractFieldMapperMapperBuilder<S, ?, K>> builderToInject = new HashMap<ConstructorPropertyMeta<T, ?>, AbstractFieldMapperMapperBuilder<S, ?, K>>();
		propertyMappingsBuilder.forEachSubProperties(new ForEachCallBack<PropertyMapping<T,?, K, FieldMapperColumnDefinition<K, S>>>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> t) {
				PropertyMeta<T, ?> pm  = t.getPropertyMeta();
				SubPropertyMeta<T, ?> subProp = (SubPropertyMeta<T, ?>) pm;
				PropertyMeta<T, ?> propOwner = subProp.getOwnerProperty();
				if (propOwner.isConstructorProperty()) {
					AbstractFieldMapperMapperBuilder<S, ?, K> builder = builderToInject.get(propOwner);
					if (builder == null) {
						builder = newSubBuilder(propOwner.getType(), propOwner.getClassMeta());
						builderToInject.put((ConstructorPropertyMeta<T, ?>) propOwner, builder);
					}
					addPropertyBuilder(t, subProp, builder);
				}
			}

			@SuppressWarnings("unchecked")
			private <P> void addPropertyBuilder(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> t,
												SubPropertyMeta<T, ?> subProp,
												AbstractFieldMapperMapperBuilder<S, ?, K> builder) {
				((AbstractFieldMapperMapperBuilder<S, P, K>)builder).addMapping(t.getColumnKey(), ((SubPropertyMeta<T, P>)subProp).getSubProperty());
			}
		});
		
		for(Entry<ConstructorPropertyMeta<T, ?>, AbstractFieldMapperMapperBuilder<S, ?, K>> e : builderToInject.entrySet()) {
            addConstructorMapper(injections, fieldMappers, e);
		}
		
		return new Tuple2<Map<ConstructorParameter, Getter<S, ?>>, FieldMapper<S, T>[]>(injections, fieldMappers.toArray(new FieldMapper[0]));
	}

    @SuppressWarnings("unchecked")
    private <P> void addConstructorMapper(Map<ConstructorParameter, Getter<S, ?>> injections, List<FieldMapper<S, T>> fieldMappers, Entry<ConstructorPropertyMeta<T, ?>, AbstractFieldMapperMapperBuilder<S, ?, K>> e) {
        AbstractFieldMapperMapperBuilder<S, P, K> mapperBuilder = (AbstractFieldMapperMapperBuilder<S, P, K>) e.getValue();
        ConstructorPropertyMeta<T, P> propertyMeta = (ConstructorPropertyMeta<T, P>) e.getKey();

        Mapper<S, P> mapper = mapperBuilder.mapper();
        Getter<S, P> subMapperGetter = new MapperGetterAdapter<S, P>(mapper);
        injections.put(propertyMeta.getConstructorParameter(), subMapperGetter);


        Getter<T, P> getter = propertyMeta.getGetter();
        if (getter != null) {
            fieldMappers.add(new MapperFieldMapper<S, T, P>(mapper, null, getter));
        }
    }


	@SuppressWarnings("unchecked")
	protected void _addMapping(K key, final FieldMapperColumnDefinition<K, S> columnDefinition) {
		final FieldMapperColumnDefinition<K, S> composedDefinition = FieldMapperColumnDefinition.compose(columnDefinition, columnDefinitions.getColumnDefinition(key));
		final K mappedColumnKey = composedDefinition.rename(key);

		if (columnDefinition.getCustomFieldMapper() != null) {
			_addMapper((FieldMapper<S, T>) columnDefinition.getCustomFieldMapper());
		} else {
			propertyMappingsBuilder.addProperty(mappedColumnKey, composedDefinition);
		}
	}


	protected <P> void addMapping(K columnKey, PropertyMeta<T, P> prop) {
		propertyMappingsBuilder.addProperty(columnKey, identity, prop);
	}


	@SuppressWarnings("unchecked")
	public final FieldMapper<S, T>[] fields() {
		final List<FieldMapper<S, T>> fields = new ArrayList<FieldMapper<S, T>>();
		
		final Map<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>, AbstractFieldMapperMapperBuilder<S, ?, K>> buildersByOwner =
				new HashMap<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>, AbstractFieldMapperMapperBuilder<S,?,K>>();
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K, S>>>() {
			
			final Map<String, AbstractFieldMapperMapperBuilder<S, ?, K>> builders = new HashMap<String, AbstractFieldMapperMapperBuilder<S,?,K>>();
			@Override
			public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> t) {
				if (t == null) return;
				PropertyMeta<T, ?> meta = t.getPropertyMeta();
				if (meta == null || (meta instanceof DirectClassMeta.DirectPropertyMeta)) return;
				if (meta.isSubProperty()) {
					addSubProperty(t,  (SubPropertyMeta<T, ?>) meta, t.getColumnKey());
				} else if (!meta.isConstructorProperty()) {
					fields.add(newFieldMapper(t));
				}
			}
			private <P> void addSubProperty(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> pm,  SubPropertyMeta<T, ?> subPropertyMeta, K key) {
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
		
		for(Entry<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>, AbstractFieldMapperMapperBuilder<S, ?, K>> e : buildersByOwner.entrySet()) {
			SubPropertyMeta<T, ?> prop = (SubPropertyMeta<T, ?>) e.getKey().getPropertyMeta();
			fields.add(newSubFieldMapper(prop.getOwnerProperty(), e.getValue(), e.getKey().getColumnKey()));
		}

		for(FieldMapper<S, T> mapper : additionalMappers) {
			fields.add(mapper);
		}
		
		return fields.toArray(new FieldMapper[fields.size()]);
	}

    protected K findKey(String columnName) {
        for(K k : propertyMappingsBuilder.getKeys()) {
            if (k.getName().equalsIgnoreCase(columnName)) {
                return k;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	private <P> FieldMapper<S, T> newSubFieldMapper(PropertyMeta<T, ?> prop,
			AbstractFieldMapperMapperBuilder<S, ?, K> builder, K key) {
		Setter<T, P> setter = (Setter<T, P>) prop.getSetter();
        Getter<T, P> getter = (Getter<T, P>) prop.getGetter();
		return newFieldMapper((AbstractFieldMapperMapperBuilder<S, P, K>)builder, setter, getter, key);
	}

	private <P> FieldMapper<S, T> newFieldMapper(
			AbstractFieldMapperMapperBuilder<S, P, K> builder,
			Setter<T, P> setter, Getter<T, P> getter,  K key) {
		FieldMapper<S, T> fm =  new MapperFieldMapper<S, T, P>((AbstractMapperImpl<S, P>)builder.mapper(), setter, getter);
		if (fieldMapperErrorHandler != null) {
			fm = new FieldErrorHandlerMapper<S, T, K>(key, fm, fieldMapperErrorHandler);
		}
		return fm;
	}

	@SuppressWarnings("unchecked")
	protected <P> FieldMapper<S, T> newFieldMapper(PropertyMapping<T, P, K, FieldMapperColumnDefinition<K, S>> t) {
		FieldMapper<S, T> fieldMapper = (FieldMapper<S, T>) t.getColumnDefinition().getCustomFieldMapper();

		if (fieldMapper == null) {
			fieldMapper = fieldMapperFactory.newFieldMapper(t, fieldMapperErrorHandler, mapperBuilderErrorHandler);
		}

		if (fieldMapperErrorHandler != null && fieldMapper != null) {
			fieldMapper = new FieldErrorHandlerMapper<S, T, K>(t.getColumnKey(), fieldMapper, fieldMapperErrorHandler);
		}
		return fieldMapper;
	}

	private Getter<S, ?> getterFor(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> t, Type paramType) {
		Getter<S, ?> getter = t.getColumnDefinition().getCustomGetter();

		if (getter == null) {
			getter = getterFactory.newGetter(paramType, t.getColumnKey());
		}
		if (getter == null) {
			mapperBuilderErrorHandler.getterNotFound("Could not find getter for " + t.getColumnKey() + " type " + paramType);
		}
		return getter;
	}


	protected void _addMapper(FieldMapper<S, T> mapper) {
		additionalMappers.add(mapper);
	}

	protected abstract <ST> AbstractFieldMapperMapperBuilder<S, ST, K> newSubBuilder(Type type, ClassMeta<ST> classMeta);
	
	public abstract Mapper<S, T> mapper();

	protected void setFieldMapperErrorHandler(
			FieldMapperErrorHandler<K> errorHandler) {
		this.fieldMapperErrorHandler = errorHandler;
	}
}