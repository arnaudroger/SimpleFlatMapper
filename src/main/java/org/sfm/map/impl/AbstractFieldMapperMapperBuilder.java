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
    protected final MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder;

    public AbstractFieldMapperMapperBuilder(final Type source,
                                            final ClassMeta<T> classMeta,
                                            GetterFactory<S, K> getterFactory,
                                            FieldMapperFactory<S, K, FieldMapperColumnDefinition<K, S>> fieldMapperFactory,
                                            ColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> columnDefinitions,
                                            PropertyNameMatcherFactory propertyNameMatcherFactory,
                                            MapperBuilderErrorHandler mapperBuilderErrorHandler, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) throws MapperBuildingException {
        this.mappingContextFactoryBuilder = mappingContextFactoryBuilder;
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

        for(Entry<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>> e :
                getSubPropertyPerOwner().entrySet()) {
            if (e.getKey().isConstructorProperty()) {
                final List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> properties = e.getValue();

                final MappingContextFactoryBuilder currentBuilder = getMapperContextFactoryBuilder(properties);

                final Mapper<S, ?> mapper = subPropertyMapper(e.getKey(), properties, currentBuilder);

                ConstructorPropertyMeta<T, ?> meta = (ConstructorPropertyMeta<T, ?>) e.getKey();



                injections.put(meta.getConstructorParameter(), newMapperGetterAdapter(mapper, currentBuilder));
                fieldMappers.add(newMapperFieldMapper(properties, meta, mapper, currentBuilder));
            }
        }
		return new Tuple2<Map<ConstructorParameter, Getter<S, ?>>, FieldMapper<S, T>[]>(injections, fieldMappers.toArray(new FieldMapper[0]));
	}

    private MappingContextFactoryBuilder getMapperContextFactoryBuilder(List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> properties) {
        final List<K> subKeys = getSubKeys(properties);

        MappingContextFactoryBuilder<S, K> currentBuilder;
        if (subKeys.isEmpty()) {
            currentBuilder = mappingContextFactoryBuilder;
        } else {
            currentBuilder = mappingContextFactoryBuilder.newBuilder(subKeys);
        }
        return currentBuilder;
    }

    @SuppressWarnings("unchecked")
    private <P> FieldMapper<S, T> newMapperFieldMapper(List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> properties, PropertyMeta<T, ?> meta, Mapper<S, ?> mapper, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {

        final MapperFieldMapper<S, T, P> fieldMapper =
                new MapperFieldMapper<S, T, P>((Mapper<S, P>) mapper,
                        (Setter<T, P>) meta.getSetter(),
                        (Getter<T, P>) meta.getGetter(),
                        mappingContextFactoryBuilder.nullChecker(),
                        mappingContextFactoryBuilder.breakDetectorGetter());

        if (fieldMapperErrorHandler != null) {
            return new FieldErrorHandlerMapper<S, T, K>(properties.get(0).getColumnKey(), fieldMapper, fieldMapperErrorHandler);
        } else {
            return fieldMapper;
        }
    }

    @SuppressWarnings("unchecked")
    private List<K> getSubKeys(List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> properties) {
        List<K> keys = new ArrayList<K>();
        for(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> pm : properties) {
            SubPropertyMeta<T, ?> subPropertyMeta = (SubPropertyMeta<T, ?>) pm.getPropertyMeta();
            if (pm.getColumnDefinition().isKey() &&  !subPropertyMeta.getSubProperty().isSubProperty()) {
                keys.add(pm.getColumnKey());
            }
        }
        return keys;
    }

    @SuppressWarnings("unchecked")
    private <P> Getter<S,P> newMapperGetterAdapter(Mapper<S, ?> mapper, MappingContextFactoryBuilder<S, K> builder) {
        return new MapperGetterAdapter<S, P>((Mapper<S, P>)mapper, builder.nullChecker());
    }

	@SuppressWarnings("unchecked")
	protected void _addMapping(K key, final FieldMapperColumnDefinition<K, S> columnDefinition) {
		final FieldMapperColumnDefinition<K, S> composedDefinition = FieldMapperColumnDefinition.compose(columnDefinition, columnDefinitions.getColumnDefinition(key));
		final K mappedColumnKey = composedDefinition.rename(key);

		if (columnDefinition.getCustomFieldMapper() != null) {
			_addMapper((FieldMapper<S, T>) columnDefinition.getCustomFieldMapper());
		} else {
            final PropertyMeta<T, ?> property = propertyMappingsBuilder.addProperty(mappedColumnKey, composedDefinition);
            if (property != null && composedDefinition.isKey() && !property.isSubProperty()) {
                mappingContextFactoryBuilder.addKey(key);
            }
		}
	}


	protected <P> void addMapping(K columnKey, FieldMapperColumnDefinition<K, S> columnDefinition,  PropertyMeta<T, P> prop) {
		propertyMappingsBuilder.addProperty(columnKey, columnDefinition, prop);
	}


	@SuppressWarnings("unchecked")
	public final FieldMapper<S, T>[] fields() {
		final List<FieldMapper<S, T>> fields = new ArrayList<FieldMapper<S, T>>();

		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K, S>>>() {
			@Override
			public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> t) {
				if (t == null) return;
				PropertyMeta<T, ?> meta = t.getPropertyMeta();
				if (meta == null || (meta instanceof DirectClassMeta.DirectPropertyMeta)) return;
                 if (!meta.isConstructorProperty() && !meta.isSubProperty()) {
					fields.add(newFieldMapper(t));
				}
			}
		});

        for(Entry<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>> e :
                getSubPropertyPerOwner().entrySet()) {
            if (!e.getKey().isConstructorProperty()) {
                final MappingContextFactoryBuilder currentBuilder = getMapperContextFactoryBuilder(e.getValue());
                final Mapper<S, ?> mapper = subPropertyMapper(e.getKey(), e.getValue(), currentBuilder);
                fields.add(newMapperFieldMapper(e.getValue(), e.getKey(), mapper, currentBuilder));
            }
        }

		for(FieldMapper<S, T> mapper : additionalMappers) {
			fields.add(mapper);
		}
		
		return fields.toArray(new FieldMapper[fields.size()]);
	}


    private Map<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>> getSubPropertyPerOwner() {
        final Map<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>> subProperties = new HashMap<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>>();
        propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K, S>>>() {

            @SuppressWarnings("unchecked")
            @Override
            public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> t) {
                if (t == null) return;
                PropertyMeta<T, ?> meta = t.getPropertyMeta();
                if (meta == null) return;
                if (meta.isSubProperty()) {
                    addSubProperty(t,  (SubPropertyMeta<T, ?>) meta, t.getColumnKey());
                }
            }
            private <P> void addSubProperty(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> pm,  SubPropertyMeta<T, ?> subPropertyMeta, K key) {
                PropertyMeta<T, ?> propertyOwner = subPropertyMeta.getOwnerProperty();
                List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> props = subProperties.get(propertyOwner);
                if (props == null) {
                    props = new ArrayList<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>();
                    subProperties.put(propertyOwner, props);
                }
                props.add(pm);
            }
        });

        return subProperties;
    }

    @SuppressWarnings("unchecked")
    private <P> Mapper<S, P> subPropertyMapper(PropertyMeta<T, P> owner, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> properties, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {
        final AbstractFieldMapperMapperBuilder<S, P, K> builder = newSubBuilder(owner.getType(), owner.getClassMeta(), mappingContextFactoryBuilder);

        for(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> pm : properties) {
            final SubPropertyMeta<T, P> propertyMeta = (SubPropertyMeta<T, P>) pm.getPropertyMeta();
            final PropertyMeta<P, ?> subProperty = ((SubPropertyMeta<T, P>) propertyMeta).getSubProperty();
            builder.addMapping(pm.getColumnKey(), pm.getColumnDefinition(), subProperty);
        }
        return builder.mapper();
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

	protected abstract <ST> AbstractFieldMapperMapperBuilder<S, ST, K> newSubBuilder(Type type, ClassMeta<ST> classMeta, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder);

	public abstract Mapper<S, T> mapper();

	protected void setFieldMapperErrorHandler(
			FieldMapperErrorHandler<K> errorHandler) {
		this.fieldMapperErrorHandler = errorHandler;
	}

    protected List<K> getPrimaryKeys() {
        final List<K> primaryKeys = new ArrayList<K>();
        propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>() {
            @Override
            public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> propertyMapping) {
                if (propertyMapping.getColumnDefinition().isKey() && !propertyMapping.getPropertyMeta().isSubProperty()) {
                    primaryKeys.add(propertyMapping.getColumnKey());
                }
            }
        });
        return primaryKeys;
    }

}