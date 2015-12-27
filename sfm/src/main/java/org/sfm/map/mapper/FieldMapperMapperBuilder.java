package org.sfm.map.mapper;

import org.sfm.jdbc.impl.getter.MapperGetterAdapter;
import org.sfm.map.*;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.impl.FieldErrorHandlerMapper;
import org.sfm.map.impl.fieldmapper.ConstantSourceFieldMapperFactory;
import org.sfm.map.impl.fieldmapper.ConstantSourceFieldMapperFactoryImpl;
import org.sfm.map.impl.fieldmapper.MapperFieldMapper;
import org.sfm.reflect.*;
import org.sfm.reflect.impl.NullGetter;
import org.sfm.reflect.meta.*;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.ForEachCallBack;

import java.lang.reflect.Type;
import java.util.*;

import static org.sfm.utils.Asserts.requireNonNull;

public final class FieldMapperMapperBuilder<S, T, K extends FieldKey<K>>  {

    private static final FieldKey[] FIELD_KEYS = new FieldKey[0];

	private final Type target;

	private final ConstantSourceFieldMapperFactory<S, K> fieldMapperFactory;

	protected final PropertyMappingsBuilder<T, K,FieldMapperColumnDefinition<K>> propertyMappingsBuilder;
	protected final ReflectionService reflectionService;
	
	private final List<FieldMapper<S, T>> additionalMappers = new ArrayList<FieldMapper<S, T>>();

    private final MapperSource<? super S, K> mapperSource;
    private final MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig;
    protected final MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder;


    public FieldMapperMapperBuilder(
            final MapperSource<? super S, K> mapperSource,
            final ClassMeta<T> classMeta,
            final MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig,
            MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder) throws MapperBuildingException {
        this.mapperSource = requireNonNull("fieldMapperSource", mapperSource);
        this.mapperConfig = requireNonNull("mapperConfig", mapperConfig);
        this.mappingContextFactoryBuilder = mappingContextFactoryBuilder;
		this.fieldMapperFactory = new ConstantSourceFieldMapperFactoryImpl<S, K>(mapperSource.getterFactory());
		this.propertyMappingsBuilder =
                new PropertyMappingsBuilder<T, K, FieldMapperColumnDefinition<K>>(classMeta,
                        mapperConfig.propertyNameMatcherFactory(), mapperConfig.mapperBuilderErrorHandler(),
                        new PropertyWithSetter());
		this.target = requireNonNull("classMeta", classMeta).getType();
		this.reflectionService = requireNonNull("classMeta", classMeta).getReflectionService();
	}

    @SuppressWarnings("unchecked")
    public final FieldMapperMapperBuilder<S, T, K> addMapping(K key, final FieldMapperColumnDefinition<K> columnDefinition) {
        final FieldMapperColumnDefinition<K> composedDefinition = columnDefinition.compose(mapperConfig.columnDefinitions().getColumnDefinition(key));
        final K mappedColumnKey = composedDefinition.rename(key);

        if (columnDefinition.getCustomFieldMapper() != null) {
            addMapper((FieldMapper<S, T>) columnDefinition.getCustomFieldMapper());
        } else {
            final PropertyMeta<T, ?> property = propertyMappingsBuilder.addProperty(mappedColumnKey, composedDefinition);
            if (property != null && composedDefinition.isKey()) {
                if (composedDefinition.keyAppliesTo().test(property)) {
                    mappingContextFactoryBuilder.addKey(key);
                }
            }
        }
        return this;
    }

    public Mapper<S, T> mapper() {
        FieldMapper<S, T>[] fields = fields();
        Tuple2<FieldMapper<S, T>[], Instantiator<S, T>> constructorFieldMappersAndInstantiator = getConstructorFieldMappersAndInstantiator();

        Mapper<S, T> mapper;

        if (isEligibleForAsmMapper()) {
            try {
                mapper =
                        reflectionService
                                .getAsmFactory()
                                .createMapper(
                                        getKeys(),
                                        fields, constructorFieldMappersAndInstantiator.first(),
                                        constructorFieldMappersAndInstantiator.second(),
                                        mapperSource.source(),
                                        getTargetClass()
                                );
            } catch (Throwable e) {
                if (mapperConfig.failOnAsm()) {
                    return ErrorHelper.rethrow(e);
                } else {
                    mapper = new MapperImpl<S, T>(fields, constructorFieldMappersAndInstantiator.first(), constructorFieldMappersAndInstantiator.second());
                }
            }
        } else {
            mapper = new MapperImpl<S, T>(fields, constructorFieldMappersAndInstantiator.first(), constructorFieldMappersAndInstantiator.second());
        }
        return mapper;
    }

    public boolean hasJoin() {
        return mappingContextFactoryBuilder.isRoot()
                && !mappingContextFactoryBuilder.hasNoDependentKeys();
    }
	private Class<T> getTargetClass() {
		return TypeHelper.toClass(target);
	}

	@SuppressWarnings("unchecked")
    private Tuple2<FieldMapper<S,T>[], Instantiator<S, T>> getConstructorFieldMappersAndInstantiator() throws MapperBuildingException {

		InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();
		try {
            Tuple2<Map<Parameter, Getter<? super S, ?>>, FieldMapper<S, T>[]> constructorInjections = constructorInjections();
            Map<Parameter, Getter<? super S, ?>> injections = constructorInjections.first();
            Instantiator<S, T> instantiator = instantiatorFactory.getInstantiator(mapperSource.source(), target, propertyMappingsBuilder, injections, mapperSource.getterFactory());
            return new Tuple2<FieldMapper<S, T>[], Instantiator<S, T>>(constructorInjections.second(), instantiator);
		} catch(Exception e) {
            return ErrorHelper.rethrow(e);
		}
	}

	@SuppressWarnings("unchecked")
    private Tuple2<Map<Parameter, Getter<? super S, ?>>, FieldMapper<S, T>[]> constructorInjections() {
		final Map<Parameter, Getter<? super S, ?>> injections = new HashMap<Parameter, Getter<? super S, ?>>();
		final List<FieldMapper<S, T>> fieldMappers = new ArrayList<FieldMapper<S, T>>();
		propertyMappingsBuilder.forEachConstructorProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K>>>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> t) {
				PropertyMeta<T, ?> pm  = t.getPropertyMeta();
                ConstructorPropertyMeta<T, ?> cProp = (ConstructorPropertyMeta<T, ?>) pm;
                Parameter parameter = cProp.getParameter();
                injections.put(parameter, getterFor(t, parameter.getGenericType()));
			}
		});

        for(Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>>> e :
                getSubPropertyPerOwner()) {
            if (e.first().isConstructorProperty()) {
                final List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> properties = e.second();

                final MappingContextFactoryBuilder currentBuilder = getMapperContextFactoryBuilder(e.first(), properties);

                final Mapper<S, ?> mapper = subPropertyMapper(e.first(), properties, currentBuilder);

                ConstructorPropertyMeta<T, ?> meta = (ConstructorPropertyMeta<T, ?>) e.first();

                injections.put(meta.getParameter(), newMapperGetterAdapter(mapper, currentBuilder));
                fieldMappers.add(newMapperFieldMapper(properties, meta, mapper, currentBuilder));
            }
        }
		return new Tuple2<Map<Parameter, Getter<? super S, ?>>, FieldMapper<S, T>[]>(injections, fieldMappers.toArray(new FieldMapper[0]));
	}

    private MappingContextFactoryBuilder getMapperContextFactoryBuilder(PropertyMeta<?, ?> owner, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> properties) {
        final List<K> subKeys = getSubKeys(properties);
        return mappingContextFactoryBuilder.newBuilder(subKeys, owner);
    }

    @SuppressWarnings("unchecked")
    private <P> FieldMapper<S, T> newMapperFieldMapper(List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> properties, PropertyMeta<T, ?> meta, Mapper<S, ?> mapper, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {

        final Getter<T, P> getter = (Getter<T, P>) meta.getGetter();
        if (mappingContextFactoryBuilder.isEmpty() && getter instanceof NullGetter) {
            throw new MapperBuildingException("Cannot get a getter on " + meta + " needed to work with join " + mappingContextFactoryBuilder);
        }
        final MapperFieldMapper<S, T, P> fieldMapper =
                new MapperFieldMapper<S, T, P>((Mapper<S, P>) mapper,
                        (Setter<T, P>) meta.getSetter(),
                        getter,
                        mappingContextFactoryBuilder.nullChecker(),
                        mappingContextFactoryBuilder.breakDetectorGetter());

        return wrapFieldMapperWithErrorHandler(properties.get(0), fieldMapper);
    }

    @SuppressWarnings("unchecked")
    private <P> Getter<S,P> newMapperGetterAdapter(Mapper<S, ?> mapper, MappingContextFactoryBuilder<S, K> builder) {
        return new MapperGetterAdapter<S, P>((Mapper<S, P>)mapper, builder.nullChecker());
    }

    // call use towards sub jdbcMapper
    // the keys are initialised
    private <P> void addMapping(K columnKey, FieldMapperColumnDefinition<K> columnDefinition,  PropertyMeta<T, P> prop) {
		propertyMappingsBuilder.addProperty(columnKey, columnDefinition, prop);
	}

	@SuppressWarnings("unchecked")
	private FieldMapper<S, T>[] fields() {
		final List<FieldMapper<S, T>> fields = new ArrayList<FieldMapper<S, T>>();

		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K>>>() {
			@Override
			public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> t) {
				if (t == null) return;
				PropertyMeta<T, ?> meta = t.getPropertyMeta();
				if (meta == null || (meta instanceof DirectClassMeta.DirectPropertyMeta)) return;
                 if (!meta.isConstructorProperty() && !meta.isSubProperty()) {
					fields.add(newFieldMapper(t));
				}
			}
		});

        for(Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>>> e :
                getSubPropertyPerOwner()) {
            if (!e.first().isConstructorProperty()) {
                final MappingContextFactoryBuilder currentBuilder = getMapperContextFactoryBuilder(e.first(), e.second());
                final Mapper<S, ?> mapper = subPropertyMapper(e.first(), e.second(), currentBuilder);
                fields.add(newMapperFieldMapper(e.second(), e.first(), mapper, currentBuilder));
            }
        }

		for(FieldMapper<S, T> mapper : additionalMappers) {
			fields.add(mapper);
		}
		
		return fields.toArray(new FieldMapper[fields.size()]);
	}


    private List<Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>>>> getSubPropertyPerOwner() {

        final List<Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>>>> subPropertiesList = new ArrayList<Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>>>>();

        propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K>>>() {

            @SuppressWarnings("unchecked")
            @Override
            public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> t) {
                if (t == null) return;
                PropertyMeta<T, ?> meta = t.getPropertyMeta();
                if (meta == null) return;
                if (meta.isSubProperty()) {
                    addSubProperty(t, (SubPropertyMeta<T, ?, ?>) meta, t.getColumnKey());
                }
            }
            private <P> void addSubProperty(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> pm,  SubPropertyMeta<T, ?, ?> subPropertyMeta, K key) {
                PropertyMeta<T, ?> propertyOwner = subPropertyMeta.getOwnerProperty();
                List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> props = getList(propertyOwner);
                if (props == null) {
                    props = new ArrayList<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>>();
                    subPropertiesList.add(new Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>>>(propertyOwner, props));
                }
                props.add(pm);
            }

            private List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> getList(PropertyMeta<?, ?> owner) {
                for(Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>>> tuple : subPropertiesList) {
                    if (tuple.first().equals(owner)) {
                        return tuple.second();
                    }
                }
                return null;
            }
        });

        return subPropertiesList;
    }

    @SuppressWarnings("unchecked")
    private <P> Mapper<S, P> subPropertyMapper(PropertyMeta<T, P> owner, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> properties, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {
        final FieldMapperMapperBuilder<S, P, K> builder = newSubBuilder(owner.getPropertyClassMeta(), mappingContextFactoryBuilder);

        for(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> pm : properties) {
            final SubPropertyMeta<T, P,  ?> propertyMeta = (SubPropertyMeta<T, P,  ?>) pm.getPropertyMeta();
            final PropertyMeta<P, ?> subProperty = ((SubPropertyMeta<T, P, ?>) propertyMeta).getSubProperty();
            builder.addMapping(pm.getColumnKey(), pm.getColumnDefinition(), subProperty);
        }
        return builder.mapper();
    }

	@SuppressWarnings("unchecked")
	protected <P> FieldMapper<S, T> newFieldMapper(PropertyMapping<T, P, K, FieldMapperColumnDefinition<K>> t) {
		FieldMapper<S, T> fieldMapper = (FieldMapper<S, T>) t.getColumnDefinition().getCustomFieldMapper();

		if (fieldMapper == null) {
			fieldMapper = fieldMapperFactory.newFieldMapper(t, mappingContextFactoryBuilder, mapperConfig.mapperBuilderErrorHandler());
		}

        return wrapFieldMapperWithErrorHandler(t, fieldMapper);
	}

    private <P> FieldMapper<S, T> wrapFieldMapperWithErrorHandler(final PropertyMapping<T, P, K, FieldMapperColumnDefinition<K>> t, final FieldMapper<S, T> fieldMapper) {
        if (fieldMapper != null && mapperConfig.hasFieldMapperErrorHandler()) {
            return new FieldErrorHandlerMapper<S, T, K>(t.getColumnKey(), fieldMapper, mapperConfig.fieldMapperErrorHandler());
        }
        return fieldMapper;
    }

    @SuppressWarnings("unchecked")
    private Getter<? super S, ?> getterFor(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> t, Type paramType) {
		Getter<? super S, ?> getter = (Getter<? super S, ?>) t.getColumnDefinition().getCustomGetter();

		if (getter == null) {
			getter = mapperSource.getterFactory().newGetter(paramType, t.getColumnKey(), t.getColumnDefinition());
		}
		if (getter == null) {
            mapperConfig.mapperBuilderErrorHandler().accessorNotFound("Could not find getter for " + t.getColumnKey() + " type " + paramType);
		}
		return getter;
	}


    public void addMapper(FieldMapper<S, T> mapper) {
		additionalMappers.add(mapper);
	}

    private <ST> FieldMapperMapperBuilder<S, ST, K> newSubBuilder(ClassMeta<ST> classMeta, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {
        return new FieldMapperMapperBuilder<S, ST, K>(
                mapperSource,
                classMeta,
                mapperConfig,
                mappingContextFactoryBuilder);
    }

    private FieldKey<?>[] getKeys() {
        return propertyMappingsBuilder.getKeys().toArray(FIELD_KEYS);
    }

    private boolean isEligibleForAsmMapper() {
        return reflectionService.isAsmActivated()
                && propertyMappingsBuilder.size() < mapperConfig.asmMapperNbFieldsLimit();
    }

    @SuppressWarnings("unchecked")
    private List<K> getSubKeys(List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> properties) {
        List<K> keys = new ArrayList<K>();

        // look for keys property of the object
        for (PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> pm : properties) {
            SubPropertyMeta<T, ?, ?> subPropertyMeta = (SubPropertyMeta<T, ?, ?>) pm.getPropertyMeta();
            if (pm.getColumnDefinition().isKey()) {
                if (pm.getColumnDefinition().keyAppliesTo().test(subPropertyMeta.getSubProperty())) {
                    keys.add(pm.getColumnKey());
                }
            }
        }

        return keys;
    }

}