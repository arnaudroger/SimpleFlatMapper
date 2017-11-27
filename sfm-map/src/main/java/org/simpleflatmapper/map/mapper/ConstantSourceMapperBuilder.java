package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.asm.MapperAsmFactory;
import org.simpleflatmapper.map.fieldmapper.MapperFieldMapper;
import org.simpleflatmapper.map.impl.GetterMapper;
import org.simpleflatmapper.map.impl.JoinUtils;
import org.simpleflatmapper.map.property.DefaultValueProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.impl.FieldErrorHandlerMapper;
import org.simpleflatmapper.map.fieldmapper.ConstantSourceFieldMapperFactory;
import org.simpleflatmapper.map.fieldmapper.ConstantSourceFieldMapperFactoryImpl;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.getter.BiFunctionGetter;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ConstructorPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SelfPropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ErrorDoc;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.Named;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Type;
import java.util.*;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class ConstantSourceMapperBuilder<S, T, K extends FieldKey<K>>  {

    private static final FieldKey[] FIELD_KEYS = new FieldKey[0];

	private final Type target;

	private final ConstantSourceFieldMapperFactory<S, K> fieldMapperFactory;

	protected final PropertyMappingsBuilder<T, K,FieldMapperColumnDefinition<K>> propertyMappingsBuilder;
	protected final ReflectionService reflectionService;

	private final List<FieldMapper<S, T>> additionalMappers = new ArrayList<FieldMapper<S, T>>();

    private final MapperSource<? super S, K> mapperSource;
    private final MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig;
    protected final MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder;

    private final KeyFactory<K> keyFactory;


    public ConstantSourceMapperBuilder(
            final MapperSource<? super S, K> mapperSource,
            final ClassMeta<T> classMeta,
            final MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig,
            MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder,
            KeyFactory<K> keyFactory) throws MapperBuildingException {
                this(mapperSource, classMeta, mapperConfig, mappingContextFactoryBuilder, keyFactory, null);
    }

    public ConstantSourceMapperBuilder(
            final MapperSource<? super S, K> mapperSource,
            final ClassMeta<T> classMeta,
            final MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig,
            MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder,
            KeyFactory<K> keyFactory, PropertyFinder<T> propertyFinder) throws MapperBuildingException {
        this.mapperSource = requireNonNull("fieldMapperSource", mapperSource);
        this.mapperConfig = requireNonNull("mapperConfig", mapperConfig);
        this.mappingContextFactoryBuilder = mappingContextFactoryBuilder;
		this.fieldMapperFactory = new ConstantSourceFieldMapperFactoryImpl<S, K>(mapperSource.getterFactory(), ConverterService.getInstance(), mapperSource.source());
        this.keyFactory = keyFactory;
        this.propertyMappingsBuilder =
                PropertyMappingsBuilder.of(classMeta, mapperConfig, PropertyWithSetterOrConstructor.INSTANCE, propertyFinder);
		this.target = requireNonNull("classMeta", classMeta).getType();
		this.reflectionService = requireNonNull("classMeta", classMeta).getReflectionService();
	}

    @SuppressWarnings("unchecked")
    public final ConstantSourceMapperBuilder<S, T, K> addMapping(K key, final FieldMapperColumnDefinition<K> columnDefinition) {
        final FieldMapperColumnDefinition<K> composedDefinition = columnDefinition.compose(mapperConfig.columnDefinitions().getColumnDefinition(key));
        final K mappedColumnKey = composedDefinition.rename(key);

        if (columnDefinition.getCustomFieldMapper() != null) {
            addMapper((FieldMapper<S, T>) columnDefinition.getCustomFieldMapper());
        } else {
            PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> propertyMapping = propertyMappingsBuilder.addProperty(mappedColumnKey, composedDefinition);
            if (propertyMapping != null) {
                FieldMapperColumnDefinition<K> effectiveColumnDefinition = propertyMapping.getColumnDefinition();
                if (effectiveColumnDefinition.isKey() && effectiveColumnDefinition.keyAppliesTo().test(propertyMapping.getPropertyMeta())) {
                    mappingContextFactoryBuilder.addKey(key);
                }
            }
        }
        return this;
    }

    public Mapper<S, T> mapper() {
        // look for property with a default value property but no definition.
        mapperConfig
                .columnDefinitions()
                .forEach(
                        DefaultValueProperty.class,
                        new BiConsumer<Predicate<? super K>, DefaultValueProperty>() {
                            @Override
                            public void accept(Predicate<? super K> predicate, DefaultValueProperty columnProperty) {
                                if (propertyMappingsBuilder.hasKey(predicate)){
                                    return;
                                }
                                if (predicate instanceof Named) {
                                    String name = ((Named)predicate).getName();
                                    GetterProperty getterProperty =
                                            new GetterProperty(new ConstantGetter<S, Object>(columnProperty.getValue()), mapperSource.source(), columnProperty.getValue().getClass());

                                    final FieldMapperColumnDefinition<K> columnDefinition =
                                            FieldMapperColumnDefinition.<K>identity().add(columnProperty,
                                                    getterProperty);
                                    propertyMappingsBuilder.addPropertyIfPresent(keyFactory.newKey(name, propertyMappingsBuilder.maxIndex() + 1), columnDefinition);
                                }
                            }
                        });


        FieldMapper<S, T>[] fields = fields();
        InstantiatorAndFieldMappers constructorFieldMappersAndInstantiator = getConstructorFieldMappersAndInstantiator();

        Mapper<S, T> mapper;

        if (isEligibleForAsmMapper()) {
            try {
                mapper =
                        reflectionService
                                .getAsmFactory()
                                .registerOrCreate(MapperAsmFactory.class,
                                        new UnaryFactory<AsmFactory, MapperAsmFactory>() {
                                            @Override
                                            public MapperAsmFactory newInstance(AsmFactory asmFactory) {
                                                return new MapperAsmFactory(asmFactory);
                                            }
                                        })
                                .createMapper(
                                        getKeys(),
                                        fields, constructorFieldMappersAndInstantiator.fieldMappers,
                                        constructorFieldMappersAndInstantiator.instantiator,
                                        mapperSource.source(),
                                        getTargetClass()
                                );
            } catch (Throwable e) {
                if (mapperConfig.failOnAsm()) {
                    return ErrorHelper.rethrow(e);
                } else {
                    mapper = new MapperImpl<S, T>(fields, constructorFieldMappersAndInstantiator.fieldMappers, constructorFieldMappersAndInstantiator.instantiator);
                }
            }
        } else {
            mapper = new MapperImpl<S, T>(fields, constructorFieldMappersAndInstantiator.fieldMappers, constructorFieldMappersAndInstantiator.instantiator);
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
    private InstantiatorAndFieldMappers getConstructorFieldMappersAndInstantiator() throws MapperBuildingException {

		InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();
		try {
            ConstructorInjections constructorInjections = constructorInjections();
            Map<Parameter, BiFunction<? super S , ? super MappingContext<? super S>, ?>> injections = constructorInjections.parameterGetterMap;
            MapperBiInstantiatorFactory mapperBiInstantiatorFactory = new MapperBiInstantiatorFactory(instantiatorFactory);
            GetterFactory<? super S, K> getterFactory = fieldMapperAsGetterFactory();
            BiInstantiator<S, MappingContext<? super S>, T> instantiator =
                    mapperBiInstantiatorFactory.
                            <S, T, K, FieldMapperColumnDefinition<K>>
                                    getBiInstantiator(mapperSource.source(), target, propertyMappingsBuilder, injections, getterFactory, reflectionService.builderIgnoresNullValues());
            return new InstantiatorAndFieldMappers(constructorInjections.fieldMappers, instantiator);
		} catch(Exception e) {
            return ErrorHelper.rethrow(e);
		}
	}

    private GetterFactory<? super S, K> fieldMapperAsGetterFactory() {
        return new FieldMapperFactoryGetterFactoryAdapter();
    }

    @SuppressWarnings("unchecked")
    private ConstructorInjections constructorInjections() {
		final Map<Parameter, BiFunction<? super S, ? super MappingContext<? super S>, ?>> injections = new HashMap<Parameter, BiFunction<? super S, ? super MappingContext<? super S>, ?>>();
		final List<FieldMapper<S, T>> fieldMappers = new ArrayList<FieldMapper<S, T>>();
		propertyMappingsBuilder.forEachConstructorProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K>>>() {

            @SuppressWarnings("unchecked")
			@Override
			public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> propertyMapping) {
                if (!isTargetForMapperFieldMapper(propertyMapping)) {
                    PropertyMeta<T, ?> pm = propertyMapping.getPropertyMeta();
                    ConstructorPropertyMeta<T, ?> cProp = (ConstructorPropertyMeta<T, ?>) pm;
                    Parameter parameter = cProp.getParameter();
                    Getter<? super S, ?> getter =
                            fieldMapperFactory.getGetterFromSource(propertyMapping.getColumnKey(), pm.getPropertyType(), propertyMapping.getColumnDefinition(), pm.getPropertyClassMetaSupplier());
                    if (NullGetter.isNull(getter)) {
                        mapperConfig.mapperBuilderErrorHandler()
                                .accessorNotFound("Could not find getter for " + propertyMapping.getColumnKey() + " type "
                                        + propertyMapping.getPropertyMeta().getPropertyType()
                                        + " path " + propertyMapping.getPropertyMeta().getPath()
                                        + " See " + ErrorDoc.toUrl("FMMB_GETTER_NOT_FOUND"));
                    } else {
                        injections.put(parameter, new BiFunctionGetter<S, MappingContext<? super S>, Object>(getter));
                    }
                    if (!NullSetter.isNull(cProp.getSetter())) {
                        fieldMappers.add(fieldMapperFactory.newFieldMapper(propertyMapping, mappingContextFactoryBuilder, mapperConfig.mapperBuilderErrorHandler()));
                    }
                }
			}
		});

        for(PropertyPerOwner e :
                getSubPropertyPerOwner()) {
            if (e.owner.isConstructorProperty()) {
                final List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> properties = e.propertyMappings;

                final MappingContextFactoryBuilder currentBuilder = getMapperContextFactoryBuilder(e.owner, properties);

                final Mapper<S, ?> mapper;
                if (properties.size() == 1 && JoinUtils.isArrayElement(properties.get(0).getPropertyMeta())) {
                    mapper = getterPropertyMapper(e.owner, properties.get(0));
                } else {
                    mapper = subPropertyMapper(e.owner, properties, currentBuilder);
                }
                ConstructorPropertyMeta<T, ?> meta = (ConstructorPropertyMeta<T, ?>) e.owner;
                injections.put(meta.getParameter(), newMapperGetterAdapter(mapper, currentBuilder));
                fieldMappers.add(newMapperFieldMapper(properties, meta, mapper, currentBuilder));
            }
        }
		return new ConstructorInjections(injections, fieldMappers.toArray(new FieldMapper[0]));
	}

    private <P> Mapper<S, P> getterPropertyMapper(PropertyMeta<T, P> owner, PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> propertyMapping) {
        PropertyMeta<T, ?> pm = propertyMapping.getPropertyMeta();
        final Getter<? super S, P> getter =
                (Getter<? super S, P>) fieldMapperFactory.getGetterFromSource(propertyMapping.getColumnKey(), pm.getPropertyType(), propertyMapping.getColumnDefinition(), pm.getPropertyClassMetaSupplier());

        return new GetterMapper<S, P>(getter);
    }

    private MappingContextFactoryBuilder getMapperContextFactoryBuilder(PropertyMeta<?, ?> owner, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> properties) {
        final List<K> subKeys = getSubKeys(properties);
        return mappingContextFactoryBuilder.newBuilder(subKeys, owner);
    }

    @SuppressWarnings("unchecked")
    private <P> FieldMapper<S, T> newMapperFieldMapper(List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> properties, PropertyMeta<T, ?> meta, Mapper<S, ?> mapper, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {
        final MapperFieldMapper<S, T, P> fieldMapper =
                new MapperFieldMapper<S, T, P>((Mapper<S, P>) mapper,
                        (Setter<T, P>) meta.getSetter(),
                        mappingContextFactoryBuilder.nullChecker(),
                        mappingContextFactoryBuilder.currentIndex());

        return wrapFieldMapperWithErrorHandler(properties.get(0), fieldMapper);
    }

    @SuppressWarnings("unchecked")
    private <P> BiFunction<S, MappingContext<? super S>, P> newMapperGetterAdapter(Mapper<S, ?> mapper, MappingContextFactoryBuilder<S, K> builder) {
        return new MapperBiFunctionAdapter<S, P>((Mapper<S, P>)mapper, builder.nullChecker(), builder.currentIndex());
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
				if (t == null || isTargetForMapperFieldMapper(t)) return;
				PropertyMeta<T, ?> meta = t.getPropertyMeta();
				if (meta == null || (meta instanceof SelfPropertyMeta)) return;
                 if (!meta.isConstructorProperty() && !isTargetForMapperFieldMapper(t)) {
					fields.add(newFieldMapper(t));
				}
			}
		});

        for(PropertyPerOwner e :
                getSubPropertyPerOwner()) {
            if (!e.owner.isConstructorProperty()) {
                final MappingContextFactoryBuilder currentBuilder = getMapperContextFactoryBuilder(e.owner, e.propertyMappings);

                final Mapper<S, ?> mapper;
                if (e.propertyMappings.size() == 1 && JoinUtils.isArrayElement(e.propertyMappings.get(0).getPropertyMeta())) {
                    mapper = getterPropertyMapper(e.owner, e.propertyMappings.get(0));
                } else {
                    mapper = subPropertyMapper(e.owner, e.propertyMappings, currentBuilder);
                }
                fields.add(newMapperFieldMapper(e.propertyMappings, e.owner, mapper, currentBuilder));
            }
        }

		for(FieldMapper<S, T> mapper : additionalMappers) {
			fields.add(mapper);
		}

		return fields.toArray(new FieldMapper[0]);
	}

    private boolean isTargetForMapperFieldMapper(PropertyMapping pm) {
        return pm.getPropertyMeta().isSubProperty() || (JoinUtils.isArrayElement(pm.getPropertyMeta()) && pm.getColumnDefinition().isKey());
    }


    private List<PropertyPerOwner> getSubPropertyPerOwner() {

        final List<PropertyPerOwner> subPropertiesList = new ArrayList<PropertyPerOwner>();

        propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K>>>() {

            @SuppressWarnings("unchecked")
            @Override
            public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> t) {
                if (t == null) return;
                PropertyMeta<T, ?> meta = t.getPropertyMeta();
                if (meta == null) return;
                if (isTargetForMapperFieldMapper(t)) {
                    addSubProperty(t, meta, t.getColumnKey());
                }
            }
            private <P> void addSubProperty(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> pm,  PropertyMeta<T,  ?> propertyMeta, K key) {
                PropertyMeta<T, ?> propertyOwner = getOwner(propertyMeta);
                List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> props = getList(propertyOwner);
                if (props == null) {
                    props = new ArrayList<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>>();
                    subPropertiesList.add(new PropertyPerOwner(propertyOwner, props));
                }
                props.add(pm);
            }

            private PropertyMeta<T, ?> getOwner(PropertyMeta<T, ?> propertyMeta) {
                if (propertyMeta.isSubProperty()) {
                    return ((SubPropertyMeta)propertyMeta).getOwnerProperty();
                }
                return propertyMeta;
            }

            private List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> getList(PropertyMeta<?, ?> owner) {
                for(PropertyPerOwner tuple : subPropertiesList) {
                    if (tuple.owner.equals(owner)) {
                        return tuple.propertyMappings;
                    }
                }
                return null;
            }
        });

        return subPropertiesList;
    }

    @SuppressWarnings("unchecked")
    private <P> Mapper<S, P> subPropertyMapper(PropertyMeta<T, P> owner, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> properties, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {
        final ConstantSourceMapperBuilder<S, P, K> builder =
                newSubBuilder(owner.getPropertyClassMeta(),
                        mappingContextFactoryBuilder,
                        (PropertyFinder<P>) propertyMappingsBuilder.getPropertyFinder().getSubPropertyFinder(owner));


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

    public void addMapper(FieldMapper<S, T> mapper) {
		additionalMappers.add(mapper);
	}

    private <ST> ConstantSourceMapperBuilder<S, ST, K> newSubBuilder(
            ClassMeta<ST> classMeta,
            MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder,
            PropertyFinder<ST> propertyFinder) {
        return new ConstantSourceMapperBuilder<S, ST, K>(
                mapperSource,
                classMeta,
                mapperConfig,
                mappingContextFactoryBuilder,
                keyFactory,
                propertyFinder);
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
            
            if (pm.getPropertyMeta().isSubProperty()) {
                SubPropertyMeta<T, ?, ?> subPropertyMeta = (SubPropertyMeta<T, ?, ?>) pm.getPropertyMeta();
                if (!(JoinUtils.isArrayElement(subPropertyMeta.getSubProperty()))) {
                    // ignore ArrayElementPropertyMeta as it's a direct getter and will be managed in the setter
                    if (pm.getColumnDefinition().isKey()) {
                        if (pm.getColumnDefinition().keyAppliesTo().test(subPropertyMeta.getSubProperty())) {
                            keys.add(pm.getColumnKey());
                        }
                    }
                }
            } else {
                if (pm.getColumnDefinition().isKey()) {
                    if (pm.getColumnDefinition().keyAppliesTo().test(pm.getPropertyMeta())) {
                        keys.add(pm.getColumnKey());
                    }
                }
            }
        }

        return keys;
    }
    private class InstantiatorAndFieldMappers {
        private final FieldMapper<S, T>[] fieldMappers;
        private final BiInstantiator<S, MappingContext<? super S>, T> instantiator;

        private InstantiatorAndFieldMappers(FieldMapper<S, T>[] fieldMappers, BiInstantiator<S, MappingContext<? super S>, T> instantiator) {
            this.fieldMappers = fieldMappers;
            this.instantiator = instantiator;
        }
    }
    private class ConstructorInjections {
        private final Map<Parameter, BiFunction<? super S, ? super MappingContext<? super S>, ?>> parameterGetterMap;
        private final FieldMapper<S, T>[] fieldMappers;

        private ConstructorInjections(Map<Parameter, BiFunction<? super S, ? super MappingContext<? super S>, ?>> parameterGetterMap, FieldMapper<S, T>[] fieldMappers) {
            this.parameterGetterMap = parameterGetterMap;
            this.fieldMappers = fieldMappers;
        }
    }

    private class PropertyPerOwner {
        private final PropertyMeta<T, ?> owner;
        private final List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> propertyMappings;

        private PropertyPerOwner(PropertyMeta<T, ?> owner, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>> propertyMappings) {
            this.owner = owner;
            this.propertyMappings = propertyMappings;
        }
    }

    private class FieldMapperFactoryGetterFactoryAdapter implements GetterFactory<S, K> {
        @SuppressWarnings("unchecked")
        @Override
        public <P> Getter<S, P> newGetter(Type target, K key, Object... properties) {
            FieldMapperColumnDefinition<K> columnDefinition = FieldMapperColumnDefinition.<K>identity().add(properties);
            return (Getter<S, P>) fieldMapperFactory.getGetterFromSource(key, target , columnDefinition, new ClassMetaSupplier<P>(target));
        }
    }
    private class ClassMetaSupplier<P> implements Supplier<ClassMeta<P>> {
        private final Type target;

        public ClassMetaSupplier(Type target) {
            this.target = target;
        }

        @Override
        public ClassMeta<P> get() {
            return reflectionService.<P>getClassMeta(target);
        }
    }

}