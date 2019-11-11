package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.asm.MapperAsmFactory;
import org.simpleflatmapper.map.context.KeyAndPredicate;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.fieldmapper.MapperFieldMapper;
import org.simpleflatmapper.map.getter.NullContextualGetter;
import org.simpleflatmapper.map.impl.DiscriminatorPropertyFinder;
import org.simpleflatmapper.map.impl.GetterMapper;
import org.simpleflatmapper.map.impl.JoinUtils;
import org.simpleflatmapper.map.property.*;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.fieldmapper.ConstantSourceFieldMapperFactory;
import org.simpleflatmapper.map.fieldmapper.ConstantSourceFieldMapperFactoryImpl;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.BuilderInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.BuilderBiInstantiator;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ConstructorPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SelfPropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.util.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

import static org.simpleflatmapper.util.Asserts.requireNonNull;
import static org.simpleflatmapper.util.ErrorDoc.CSFM_GETTER_NOT_FOUND;

public final class DefaultConstantSourceMapperBuilder<S, T, K extends FieldKey<K>> extends ConstantSourceMapperBuilder<S, T, K> {

    private static final FieldKey[] FIELD_KEYS = new FieldKey[0];
    public static final FieldMapper[] EMPTY_FIELD_MAPPERS = new FieldMapper[0];

    private final Type target;

	private final ConstantSourceFieldMapperFactory<S, K> fieldMapperFactory;

	protected final PropertyMappingsBuilder<T, K> propertyMappingsBuilder;
	protected final ReflectionService reflectionService;

	private final List<FieldMapper<S, T>> additionalMappers = new ArrayList<FieldMapper<S, T>>();

    private final MapperSource<? super S, K> mapperSource;
    private final MapperConfig<K, ? extends S> mapperConfig;
    protected final MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder;

    private final KeyFactory<K> keyFactory;

    public DefaultConstantSourceMapperBuilder(
            final MapperSource<? super S, K> mapperSource,
            final ClassMeta<T> classMeta,
            final MapperConfig<K, ? extends S> mapperConfig,
            MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder,
            KeyFactory<K> keyFactory, 
            PropertyFinder<T> propertyFinder) throws MapperBuildingException {
        final ContextualGetterFactory<? super S, K> getterFactory = mapperSource.getterFactory();
        
        this.mapperSource = requireNonNull("fieldMapperSource", mapperSource);
        this.mapperConfig = requireNonNull("mapperConfig", mapperConfig);
        this.mappingContextFactoryBuilder = mappingContextFactoryBuilder;
        this.fieldMapperFactory = new ConstantSourceFieldMapperFactoryImpl<S, K>(getterFactory, ConverterService.getInstance(), mapperSource.source());
        this.keyFactory = keyFactory;
        this.propertyMappingsBuilder =
                PropertyMappingsBuilder.of(classMeta, mapperConfig, new PropertyMappingsBuilder.PropertyPredicateFactory<K>() {
                    @Override
                    public PropertyFinder.PropertyFilter predicate(final K k, final Object[] properties, final List<PropertyMappingsBuilder.AccessorNotFound> accessorNotFounds) {
                        if (k != null) {
                            final MappingContextFactoryBuilder<Object, K> mappingContextFactoryBuilder1 = new MappingContextFactoryBuilder<Object, K>(new KeySourceGetter<K, Object>() {
                                @Override
                                public Object getValue(K key, Object source) throws Exception {
                                    return null;
                                }
                            }, !mapperConfig.unorderedJoin());
                            Predicate<PropertyMeta<?, ?>> propertyMetaPredicate = new Predicate<PropertyMeta<?, ?>>() {
                                @Override
                                public boolean test(PropertyMeta<?, ?> propertyMeta) {

                                    if (!PropertyWithSetterOrConstructor.INSTANCE.test(propertyMeta))
                                        return false;

                                    try {
                                        ContextualGetter<? super S, ?> getterFromSource = getContextualGetter(propertyMeta);
                                        if (NullContextualGetter.isNull(getterFromSource)) {
                                            accessorNotFounds.add(new PropertyMappingsBuilder.AccessorNotFound(k, propertyMeta.getPath(), propertyMeta.getPropertyType(), CSFM_GETTER_NOT_FOUND, propertyMeta));
                                            return false;
                                        }
                                        return true;
                                    } catch (Exception e) {
                                        return false;
                                    }
                                }

                                public <O, P> ContextualGetter<? super S, ? extends P> getContextualGetter(PropertyMeta<O, P> propertyMeta) {
                                    return fieldMapperFactory.<P>getGetterFromSource(
                                            k,
                                            propertyMeta.getPropertyType(),
                                            FieldMapperColumnDefinition.<K>of(properties),
                                            propertyMeta.getPropertyClassMetaSupplier(),
                                            mappingContextFactoryBuilder1);
                                }
                            };
                            return new PropertyFinder.PropertyFilter(propertyMetaPredicate, PropertyWithSetterOrConstructor.INSTANCE);
                        }
                        return new PropertyFinder.PropertyFilter(PropertyWithSetterOrConstructor.INSTANCE);
                    }
                }, propertyFinder);
		this.target = requireNonNull("classMeta", classMeta).getType();
		this.reflectionService = requireNonNull("classMeta", classMeta).getReflectionService();
	}

    @Override
    @SuppressWarnings("unchecked")
    public final ConstantSourceMapperBuilder<S, T, K> addMapping(K key, final ColumnDefinition<K, ?> columnDefinition) {
        final ColumnDefinition<K, ?> composedDefinition = columnDefinition.compose(mapperConfig.columnDefinitions().getColumnDefinition(key));
        final K mappedColumnKey = composedDefinition.rename(key);

        FieldMapperProperty prop = columnDefinition.lookFor(FieldMapperProperty.class);
        if (prop != null) {
            addMapper((FieldMapper<S, T>) prop.getFieldMapper());
        } else {
            PropertyMapping<T, ?, K> propertyMapping = propertyMappingsBuilder.addProperty(mappedColumnKey, composedDefinition);
            if (propertyMapping != null) {
                ColumnDefinition<K, ?> effectiveColumnDefinition = propertyMapping.getColumnDefinition();
                if (effectiveColumnDefinition.isKey() && effectiveColumnDefinition.keyAppliesTo().test(propertyMapping.getPropertyMeta())) {
                    Predicate<S> predicate = buildKeyPredicate(propertyMapping.getPropertyMeta(), effectiveColumnDefinition.keyAppliesTo());
                    mappingContextFactoryBuilder.addKey(new KeyAndPredicate<S, K>(mappedColumnKey, predicate));
                } else if (effectiveColumnDefinition.isInferNull() && effectiveColumnDefinition.inferNullsAppliesTo().test(propertyMapping.getPropertyMeta())) {
                    Predicate<S> predicate = buildKeyPredicate(propertyMapping.getPropertyMeta(), effectiveColumnDefinition.inferNullsAppliesTo());
                    mappingContextFactoryBuilder.addInferNull(new KeyAndPredicate<S, K>(mappedColumnKey, predicate));

                }
            }
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ContextualSourceFieldMapperImpl<S, T> mapper() {
        SourceFieldMapper<S, T> mapper = sourceFieldMapper();
        return new ContextualSourceFieldMapperImpl<S, T>(mappingContextFactoryBuilder.build(), mapper);

    }

    @Override
    public SourceFieldMapper<S, T> sourceFieldMapper() {
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

        final List<String> missingProperties = new ArrayList<String>();
        //
        mapperConfig
                .columnDefinitions()
                .forEach(
                        MandatoryProperty.class,
                        new BiConsumer<Predicate<? super K>, MandatoryProperty>() {
                            @Override
                            public void accept(Predicate<? super K> predicate, MandatoryProperty columnProperty) {
                                if (!propertyMappingsBuilder.hasKey(predicate)){
                                    if (predicate instanceof Named) {
                                        missingProperties.add(((Named)predicate).getName());
                                    } else {
                                        missingProperties.add(predicate.toString());
                                    }
                                }
                            }
                        });


        if (!missingProperties.isEmpty()) {
            throw new MissingPropertyException(missingProperties);
        }
        SourceFieldMapper<S, T> mapper;

        List<InjectionParam> injectionParams = constructorInjections();

        if (isTargetForTransformer(injectionParams)) {
            mapper = buildMapperWithTransformer(injectionParams);
        } else {
            ConstructorInjections<S, T> constructorInjections = toConstructorInjections(injectionParams);
            InstantiatorAndFieldMappers<S, T> constructorFieldMappersAndInstantiator = getConstructorFieldMappersAndInstantiator(constructorInjections);
            mapper = buildMapper(targetFieldMappers(), constructorFieldMappersAndInstantiator, getKeys().toArray(FIELD_KEYS), getTargetClass(), reflectionService, mapperSource, mapperConfig);
        }
        return mapper;
    }

    private boolean isTargetForTransformer(List<InjectionParam> injectionParams) {
        return
                propertyMappingsBuilder.getClassMeta().needTransformer() 
                        || needGenericBuilder(injectionParams)
                // is aggregate and constructor injection
                       || (mapperConfig.assumeInjectionModifiesValues() && (!mappingContextFactoryBuilder.hasNoDependentKeys() && !injectionParams.isEmpty()))
                ;
    }

    @SuppressWarnings("unchecked")
    private SourceFieldMapper<S, T> buildMapperWithTransformer(List<InjectionParam> injections) {
        boolean forceGenericBuilder = needGenericBuilder(injections);

        BuilderInstantiatorDefinition mutableBuilder = getMutableBuilder();

        // already has mutable builder
        if (!forceGenericBuilder && 
                mutableBuilder != null) {
            return builderWithTransformer(injections, mutableBuilder);
        } else {
            return buildWithGenericBuilder(injections, fields(), getKeys().toArray(FIELD_KEYS));
        }
    }


 

    private boolean needGenericBuilder(List<InjectionParam> injections) {
        boolean forceGenericBuilder = false;

        // handle builder with an injection needing transformation
        for(InjectionParam ip : injections) { 
            if (ip.needTransformer()) {
                forceGenericBuilder = true;
                break;
            }
        }
        return forceGenericBuilder;
    }

    private BuilderInstantiatorDefinition getMutableBuilder() {
        List<InstantiatorDefinition> eligibleInstantiatorDefinitions = propertyMappingsBuilder.getPropertyFinder().getEligibleInstantiatorDefinitions();
        
        for(int i = 0; i < eligibleInstantiatorDefinitions.size(); i++) {
            InstantiatorDefinition instantiatorDefinition = eligibleInstantiatorDefinitions.get(i);
            
            if (instantiatorDefinition.getType() == InstantiatorDefinition.Type.BUILDER) {
                BuilderInstantiatorDefinition bid = (BuilderInstantiatorDefinition) instantiatorDefinition;
                if (bid.isMutable()) {
                    return bid;
                }
            }
        }
        
        return null;
    }


    @SuppressWarnings("unchecked")
    private SourceFieldMapper<S, T> buildWithGenericBuilder(
            List<InjectionParam> params,
            List<FieldMeta> fields,
            FieldKey<K>[] keys) {
        GenericBuilderMapping gbm = getGenericBuilderMapping(params, fields, keys);
        return buildWithGenericBuilder(gbm);
    }
    
    public GenericBuilderMapping<S, T, K> getGenericBuilderMapping() {
        return getGenericBuilderMapping(constructorInjections(), fields(), getKeys().toArray(FIELD_KEYS));
    }

    private GenericBuilderMapping<S, T, K> getGenericBuilderMapping(List<InjectionParam> params, List<FieldMeta> fields, FieldKey<K>[] keys) {
        int nbParams = params.size();
        final Parameter[] indexMapping = new Parameter[nbParams];
        final Function[] transformers = new Function[nbParams];


        List<FieldMapper<S, GenericBuilder<S, T>>> genericBuilderFieldMappers = new ArrayList<FieldMapper<S, GenericBuilder<S, T>>>();
        List<FieldMapper<S, T>> targetConstructorFieldMapper = new ArrayList<FieldMapper<S, T>>();
        List<FieldMapper<S, T>> targetFieldMappers = new ArrayList<FieldMapper<S, T>>();
        List<Setter<T, GenericBuilder<S, T>>> targetFieldSetters = new ArrayList<Setter<T, GenericBuilder<S, T>>>();

        int i = 0;
        
        // Generic builder can have all as field mapper not need for constructor injection
        // 
        for(InjectionParam p : params) {
            GenericBuilderGetterAndFieldMapper getterAndFieldMapper = p.getterAndfieldMapperGenericBuilder(i);

            if (getterAndFieldMapper != null) {
                if (getterAndFieldMapper.fieldMapper == null) {
                    throw new IllegalStateException();
                }

                genericBuilderFieldMappers.add(getterAndFieldMapper.fieldMapper);

                if (getterAndFieldMapper.fieldMapperAfterConstruct != null) {
                    targetConstructorFieldMapper.add(getterAndFieldMapper.fieldMapperAfterConstruct);
                }

                indexMapping[i] = p.parameter;
                transformers[i] = getterAndFieldMapper.transform;
                i++;
            }
        }


        for(FieldMeta fm : fields) {
            FieldGenericBuilderInfo fieldGenericBuilderInfo = fm.fieldGenericBuilderInfo(i);
            targetFieldMappers.add(fieldGenericBuilderInfo.targetFieldMapper);
            genericBuilderFieldMappers.add(fieldGenericBuilderInfo.fieldMapperGeneric);
            targetFieldSetters.add(fieldGenericBuilderInfo.fieldSetter);
            i++;
        }



        final BiInstantiator<Object[], Object, T> targetInstantiatorFromGenericBuilder = targetInstantiatorFromGenericBuilder(indexMapping, transformers);

        GenericBuildBiInstantiator<S, T> genericBuilderInstantiator = 
                new GenericBuildBiInstantiator<S, T>(
                        genericBuilderFieldMappers.<FieldMapper<S, GenericBuilder<S, T>>>toArray(EMPTY_FIELD_MAPPERS), 
                        targetInstantiatorFromGenericBuilder, 
                        targetFieldSetters.toArray(new Setter[0]));

        InstantiatorAndFieldMappers<S, GenericBuilder<S, T>> instantiatorAndFieldMappers =
                new InstantiatorAndFieldMappers<S, GenericBuilder<S, T>>(
                        new ConstructorInjections(Collections.emptyMap(), new FieldMapper[0]),
                        genericBuilderInstantiator);


        FieldMapper<S, T>[] targetFMappers = merge(targetConstructorFieldMapper.toArray(EMPTY_FIELD_MAPPERS), targetFieldMappers.toArray(EMPTY_FIELD_MAPPERS));

        return new GenericBuilderMapping<S, T, K>(
                genericBuilderInstantiator, 
                instantiatorAndFieldMappers, 
                genericBuilderFieldMappers.toArray(EMPTY_FIELD_MAPPERS),
                targetFMappers,
                keys
        );
    }

    private SourceFieldMapper<S, T> buildWithGenericBuilder(GenericBuilderMapping<S, T, K> gbm) {
        SourceFieldMapper<S, GenericBuilder<S, T>> delegate = 
                DefaultConstantSourceMapperBuilder.<S, GenericBuilder<S, T>, K>buildMapper(
                gbm.genericBuilderFieldMappers,
                gbm.instantiatorAndFieldMappers,
                gbm.keys,
                (Class<GenericBuilder<S, T>>)(Class)GenericBuilder.class,
                reflectionService,
                mapperSource,
                mapperConfig);

        return new TransformSourceFieldMapper<S, GenericBuilder<S, T>, T>(delegate, gbm.targetFieldMappers, GenericBuilder.<S, T>buildFunction());
        
    }

    public Type getTargetType() {
        return propertyMappingsBuilder.getClassMeta().getType();
    }

    public List<K> findAllDiscriminatorKeys(final Object discriminatorId) {
        final List<K> keys = new ArrayList<K>();
        propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, K>>() {
            @Override
            public void handle(PropertyMapping<T, ?, K> tkPropertyMapping) {
                ColumnDefinition<K, ?> columnDefinition = tkPropertyMapping.getColumnDefinition();
                if (columnDefinition.has(DiscriminatorColumnProperty.class)) {
                    DiscriminatorColumnProperty[] properties = columnDefinition.lookForAll(DiscriminatorColumnProperty.class);
                    for(DiscriminatorColumnProperty p : properties) {
                        if (MapperConfig.sameDiscriminatorId(discriminatorId , p.getDiscriminatorId())  &&
                                p.test(getTargetType())) {
                            keys.add(tkPropertyMapping.getColumnKey());
                            return;
                        }
                    }
                }
            }
        });
        return keys;
    }

    public static class GenericBuilderMapping<S, T, K extends FieldKey<K>> {
        public final GenericBuildBiInstantiator<S, T> genericBuilderInstantiator;
        public final InstantiatorAndFieldMappers<S, GenericBuilder<S, T>> instantiatorAndFieldMappers;
        public final FieldMapper<S, GenericBuilder<S, T>>[] genericBuilderFieldMappers;
        public final FieldMapper<S, T>[] targetFieldMappers;
        public final FieldKey<K>[] keys;


        public GenericBuilderMapping(
                GenericBuildBiInstantiator<S, T> genericBuilderInstantiator, 
                InstantiatorAndFieldMappers<S, GenericBuilder<S, T>> instantiatorAndFieldMappers, 
                FieldMapper<S, GenericBuilder<S, T>>[] genericBuilderFieldMappers, 
                FieldMapper<S, T>[] targetFieldMappers, 
                FieldKey<K>[] keys) {
            this.genericBuilderInstantiator = genericBuilderInstantiator;
            this.instantiatorAndFieldMappers = instantiatorAndFieldMappers;
            this.genericBuilderFieldMappers = genericBuilderFieldMappers;
            this.targetFieldMappers = targetFieldMappers;
            this.keys = keys;
        }
    }

    private FieldMapper<S, T>[] merge(FieldMapper<S, T>[] fieldMappers, FieldMapper<S, T>[] fields) {
        FieldMapper<S, T>[] f = new FieldMapper[fieldMappers.length + fields.length];

        System.arraycopy(fieldMappers, 0, f, 0, fieldMappers.length);
        System.arraycopy(fields, 0, f, fieldMappers.length, fields.length);

        return f;
    }
    private BiInstantiator<Object[], Object, T> targetInstantiatorFromGenericBuilder(Parameter[] indexMapping, Function[] transformers) {
        InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();


        Map<Parameter, BiFunction<? super Object[], ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Object[], ? super Object, ?>>();

        for(int i = 0; i < indexMapping.length; i++) {
            Parameter parameter = indexMapping[i];
            final int builderIndex = i;
            final Function transformer = transformers[i];
            if (transformer == null) {
                params.put(parameter, new DefaultConstantSourceMapperBuilder.TargetFromBuilderParamBiFunction(builderIndex));
            } else {
                params.put(parameter, new DefaultConstantSourceMapperBuilder.TargetFromBuilderWithTransformBiFunction(transformer, builderIndex));
            }
        }
        BiInstantiator<Object[], Object, T> targetInstantiator = instantiatorFactory.getBiInstantiator(getTargetClass(), Object[].class, Object.class,
                propertyMappingsBuilder.getPropertyFinder().getEligibleInstantiatorDefinitions(), params, reflectionService.isAsmActivated(), reflectionService.builderIgnoresNullValues());

        return targetInstantiator;
    }


    public static class GenericBuildBiInstantiator<S, T> implements BiInstantiator<S, MappingContext<? super S>, GenericBuilder<S, T>> {
        private final BiInstantiator<Object[], Object, T> targetInstantiatorFromGenericBuilder;
        private final Setter<T, GenericBuilder<S, T>>[] targetFieldSetters;
        private final FieldMapper<S, GenericBuilder<S, T>>[] genericBuilderFieldMappers;

        public GenericBuildBiInstantiator(
                FieldMapper<S, GenericBuilder<S, T>>[] genericBuilderFieldMappers, 
                BiInstantiator<Object[], Object, T> targetInstantiatorFromGenericBuilder, 
                Setter<T, GenericBuilder<S, T>>[] targetFieldSetters) {
            this.genericBuilderFieldMappers = genericBuilderFieldMappers;
            this.targetInstantiatorFromGenericBuilder = targetInstantiatorFromGenericBuilder;
            this.targetFieldSetters = targetFieldSetters;
        }

        @Override
        public GenericBuilder<S, T> newInstance(S o, MappingContext<? super S> o2) {
            return new GenericBuilder<S, T>(genericBuilderFieldMappers, targetInstantiatorFromGenericBuilder, targetFieldSetters);
        }
    }

    @SuppressWarnings("unchecked")
    private SourceFieldMapper<S, T> builderWithTransformer(final List<InjectionParam> params, final BuilderInstantiatorDefinition builder) {
        final FieldMapper[] fields = targetFieldMappers();
        final Method buildMethod = builder.getBuildMethod();
        final Class<?> targetClass = buildMethod.getDeclaringClass();
        final Function f = Modifier.isStatic(buildMethod.getModifiers()) ? new StaticMethodFunction(buildMethod) : new MethodFunction(buildMethod);

        ConstructorInjections constructorInjections = toConstructorInjections(params);
        InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();

        final BuilderBiInstantiator builderBiInstantiator = instantiatorFactory.builderBiInstantiator(builder, MapperBiInstantiatorFactory.convertToBiInstantiator(constructorInjections.parameterGetterMap), reflectionService.isAsmActivated(), reflectionService.builderIgnoresNullValues());
        
        InstantiatorAndFieldMappers newConstantSourceMapperBuilder = 
                new InstantiatorAndFieldMappers(constructorInjections, new BiInstantiator() {
            @Override
            public Object newInstance(Object o, Object o2) throws Exception {
                return builderBiInstantiator.newInitialisedBuilderInstace(o, o2);
            }
        });
        SourceFieldMapper delegate = buildMapper(fields, newConstantSourceMapperBuilder, getKeys().toArray(FIELD_KEYS), targetClass, reflectionService, mapperSource, mapperConfig);
        return new TransformSourceFieldMapper<S, Object, T>(delegate, fields, f);
    }

    public static <S, T, K extends FieldKey<K>> SourceFieldMapper<S, T> buildMapper(
             FieldMapper<S, T>[] fields, 
             InstantiatorAndFieldMappers<S, T> constructorFieldMappersAndInstantiator,
             FieldKey<?>[] keys,
             Class<T> target, 
             ReflectionService reflectionService, 
             MapperSource<? super S, K> mapperSource, 
             MapperConfig<K, ? extends S> mapperConfig) {
        SourceFieldMapper<S, T> mapper;

        if (reflectionService.isAsmActivated() 
                && fields.length + constructorFieldMappersAndInstantiator.constructorInjections.parameterGetterMap.size() < mapperConfig.asmMapperNbFieldsLimit()) {
            try {
                MapperAsmFactory mapperAsmFactory = reflectionService
                        .getAsmFactory(target.getClassLoader())
                        .registerOrCreate(MapperAsmFactory.class,
                                new UnaryFactory<AsmFactory, MapperAsmFactory>() {
                                    @Override
                                    public MapperAsmFactory newInstance(AsmFactory asmFactory) {
                                        return new MapperAsmFactory(asmFactory);
                                    }
                                });
                mapper =
                        mapperAsmFactory
                                .createMapper(
                                        keys,
                                        fields,
                                        constructorFieldMappersAndInstantiator.constructorInjections.fieldMappers,
                                        constructorFieldMappersAndInstantiator.instantiator,
                                        mapperSource.source(),
                                        target);
            } catch (Throwable e) {
                if (mapperConfig.failOnAsm()) {
                    return ErrorHelper.rethrow(e);
                } else {
                    mapper = new MapperImpl<S, T>(fields, constructorFieldMappersAndInstantiator.constructorInjections.fieldMappers, constructorFieldMappersAndInstantiator.instantiator);
                }
            }
        } else {
            mapper = new MapperImpl<S, T>(fields, constructorFieldMappersAndInstantiator.constructorInjections.fieldMappers, constructorFieldMappersAndInstantiator.instantiator);
        }
        return mapper;
    }

    @Override
    public boolean isRootAggregate() {
        return mappingContextFactoryBuilder.isRoot()
                && !mappingContextFactoryBuilder.hasNoDependentKeys();
    }
	private Class<T> getTargetClass() {
		return TypeHelper.toClass(target);
	}


    private <T> ConstructorInjections<S, T> toConstructorInjections(List<InjectionParam> params) throws MapperBuildingException {

        Map<Parameter, ContextualGetter<? super S ,  ?>> injections = new HashMap<Parameter, ContextualGetter<? super S, ?>>();
        List<FieldMapper<S, T>> fieldMappers = new ArrayList<FieldMapper<S, T>>();

        for(int i = 0; i < params.size(); i++) {
            InjectionParam p = params.get(i);
            GetterAndFieldMapper getterAndFieldMapper = p.getterAndfieldMapper();
            injections.put(p.parameter, getterAndFieldMapper.getter);
            if  (getterAndFieldMapper.fieldMapper != null) {
                fieldMappers.add((FieldMapper<S, T>) getterAndFieldMapper.fieldMapper);
            }
        }

        return new ConstructorInjections<S, T>(injections, fieldMappers.toArray(EMPTY_FIELD_MAPPERS));
    }
	
	@SuppressWarnings("unchecked")
    private InstantiatorAndFieldMappers<S, T> getConstructorFieldMappersAndInstantiator(ConstructorInjections<S, T> constructorInjections) throws MapperBuildingException {
 
		InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();
		try {
            Map<Parameter, ContextualGetter<? super S , ?>> injections = constructorInjections.parameterGetterMap;
            MapperBiInstantiatorFactory mapperBiInstantiatorFactory = new MapperBiInstantiatorFactory(instantiatorFactory);
            ContextualGetterFactory<? super S, K> getterFactory = fieldMapperAsGetterFactory();
            BiInstantiator<S, MappingContext<?>, T> instantiator =
                    mapperBiInstantiatorFactory.
                            <S, T, K>
                                    getBiInstantiator(mapperSource.source(), target, propertyMappingsBuilder, injections, getterFactory, reflectionService.builderIgnoresNullValues(), mappingContextFactoryBuilder);
            return new InstantiatorAndFieldMappers(constructorInjections, instantiator);
		} catch(Exception e) {
            return ErrorHelper.rethrow(e);
		}
	}

    private ContextualGetterFactory<? super S, K> fieldMapperAsGetterFactory() {
        return new FieldMapperFactoryGetterFactoryAdapter(mapperConfig.fieldMapperErrorHandler());
    }

    @SuppressWarnings("unchecked")
    /**
     * Build the list of constructor prop and fieldmappers
     */
    private List<InjectionParam> constructorInjections() {
		
		final List<InjectionParam> injectionParams = new ArrayList<InjectionParam>();
		final Set<Parameter> parameters = new HashSet<Parameter>();
		propertyMappingsBuilder.forEachConstructorProperties(new ForEachCallBack<PropertyMapping<T,?,K>>() {
            @SuppressWarnings("unchecked")
			@Override
			public void handle(PropertyMapping<T, ?, K> propertyMapping) {
                if (!isTargetForMapperFieldMapper(propertyMapping) && ! propertyMapping.getPropertyMeta().isNonMapped()) {
                    PropertyMeta<T, ?> pm = propertyMapping.getPropertyMeta();
                    ConstructorPropertyMeta<T, ?> cProp = (ConstructorPropertyMeta<T, ?>) pm;
                    injectionParams.add(new ConstructorParam(cProp.getParameter(), cProp, propertyMapping));
                    parameters.add(cProp.getParameter());
                }
			}
		});

        for(PropertyPerOwner e :
                getSubPropertyPerOwner()) {
            if (e.owner.isConstructorProperty()) {
                ConstructorPropertyMeta<T, ?> meta = (ConstructorPropertyMeta<T, ?>) e.owner;
                // ignore if no mapped properties
                if (hasMappedProperties(e.propertyMappings)) {
                    injectionParams.add(new SubPropertyParam(meta.getParameter(), meta, e.propertyMappings, this));
                    parameters.add(meta.getParameter());
                }

            }
        }

        addContextParam(injectionParams, parameters);

        return injectionParams;
	}

    private boolean hasMappedProperties(List<PropertyMapping<T, ?, K>> propertyMappings) {
        for(PropertyMapping<T, ?, K> pm : propertyMappings) {
            if (!pm.getPropertyMeta().isNonMapped()) return true;
        }
        return false;
    }

    private void addContextParam(List<InjectionParam> injectionParams, Set<Parameter> parameters) {
        Parameter mappingContext = null;
        for(InstantiatorDefinition id : propertyMappingsBuilder.getPropertyFinder().getEligibleInstantiatorDefinitions()) {
            for(Parameter p : id.getParameters()) {
                if (TypeHelper.areEquals(p.getType(), Context.class) && ! parameters.contains(p)) {
                    if (mappingContext != null && ! p.equals(mappingContext)) {
                        // multiple context ignore to avoid constructor selection issue
                        return;
                    }
                    mappingContext = p;
                }
            }
        }
        if (mappingContext != null) {
            injectionParams.add(new ContextParam(mappingContext, null));
        }
    }

    private <P> SourceMapper<S, P> getterPropertyMapper(PropertyMeta<T, P> owner, PropertyMapping<T, ?, K> propertyMapping) {
        PropertyMeta<T, ?> pm = propertyMapping.getPropertyMeta();
        final ContextualGetter<? super S, P> getter =
                (ContextualGetter<? super S, P>) fieldMapperFactory.getGetterFromSource(propertyMapping.getColumnKey(), pm.getPropertyType(), propertyMapping.getColumnDefinition(), pm.getPropertyClassMetaSupplier(), mappingContextFactoryBuilder);

        return new GetterMapper<S, P>(getter);
    }

    private MappingContextFactoryBuilder getMapperContextFactoryBuilder(PropertyMeta<?, ?> owner, List<PropertyMapping<T, ?, K>> properties) {
        final List<KeyAndPredicate<S, K>> subKeys = getSubKeys(properties);
        final List<KeyAndPredicate<S, K>> inferNullColumns = getInferNulls(properties);
        return mappingContextFactoryBuilder.newBuilder(subKeys, inferNullColumns, owner);
    }

    @SuppressWarnings("unchecked")
    private <P> FieldMapper<S, T> newMapperFieldMapper(List<PropertyMapping<T, ?, K>> properties, PropertyMeta<T, ?> meta, SourceMapper<S, ?> mapper, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {
        return newMapperFieldMapper(properties, (Setter<T, P>) meta.getSetter(), mapper, mappingContextFactoryBuilder);
    }

    private <P> FieldMapper<S, T> newMapperFieldMapper(List<PropertyMapping<T, ?, K>> properties, Setter<T, P> setter, SourceMapper<S, ?> mapper, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {
        final MapperFieldMapper fieldMapper =
                new MapperFieldMapper(mapper,
                        (Setter<T, P>)setter,
                        mappingContextFactoryBuilder.nullChecker(),
                        mappingContextFactoryBuilder.currentIndex());

        return wrapFieldMapperWithErrorHandler(properties.get(0).getColumnKey(), fieldMapper);
    }

    @SuppressWarnings("unchecked")
    private <P> ContextualGetter<S,  P> newMapperGetterAdapter(SourceMapper<S, ?> mapper, MappingContextFactoryBuilder<S, K> builder) {
        return new MapperFieldMapperGetterAdapter<S, P>((SourceMapper<S, P>)mapper, builder.nullChecker(), builder.currentIndex());
    }

    // call use towards sub jdbcMapper
    // the keys are initialised
    protected  <P> void addMapping(K columnKey, ColumnDefinition<K, ?> columnDefinition,  PropertyMeta<T, P> prop) {
		propertyMappingsBuilder.addProperty(columnKey, columnDefinition, prop);
	}

    private FieldMapper<S, T>[] targetFieldMappers() {
        List<FieldMeta> fields = fields();
        FieldMapper<S, T>[] fieldMappers = new FieldMapper[fields.size()];
        
        for(int i = 0; i < fields.size(); i++) {
            fieldMappers[i] = fields.get(i).targetFieldMapper();
        }
        
        return fieldMappers;
    }
    
    @SuppressWarnings("unchecked")
	private List<FieldMeta> fields() {
		final List<FieldMeta> fields = new ArrayList<FieldMeta>();

		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,K>>() {
			@Override
			public void handle(PropertyMapping<T, ?, K> t) {
				if (t == null || isTargetForMapperFieldMapper(t)) return;
				PropertyMeta<T, ?> meta = t.getPropertyMeta();
				if (meta == null || (meta instanceof SelfPropertyMeta)) return;
                 if (!meta.isConstructorProperty() && !isTargetForMapperFieldMapper(t) && !meta.isNonMapped()) {
					fields.add(new PropertyFieldMeta(t));
				}
			}
		});

        List<PropertyPerOwner> subPropertyPerOwner = getSubPropertyPerOwner();
        for(PropertyPerOwner e : subPropertyPerOwner) {
            if (!e.owner.isConstructorProperty()) {
                List<PropertyMapping<T, ?, K>> propertyMappings = filterNonMappedAndCompress(e.propertyMappings);

                if (propertyMappings.isEmpty()) { // non mapped property
                    continue; // ignore no actual prop
                }

                final SourceMapper<S, ?> mapper;
                final MappingContextFactoryBuilder currentBuilder = getMapperContextFactoryBuilder(e.owner, e.propertyMappings);


                if (propertyMappings.size() == 1 && JoinUtils.isArrayElement(propertyMappings.get(0).getPropertyMeta())) {
                    mapper = getterPropertyMapper(e.owner, propertyMappings.get(0));
                } else {
                    mapper = subPropertyMapper(e.owner, e.propertyMappings, currentBuilder);
                }
                fields.add(new SubPropertyFieldMeta(mapper, e.propertyMappings, e.owner, currentBuilder));
            }
        }

		for(FieldMapper<S, T> mapper : additionalMappers) {
			fields.add(new FieldMapperFieldMeta(mapper));
		}

		return fields;
	}

    private List<PropertyMapping<T, ?, K>> filterNonMappedAndCompress(List<PropertyMapping<T, ?, K>> propertyMappings) {
        ArrayList<PropertyMapping<T, ?, K>> filtered = new ArrayList<PropertyMapping<T, ?, K>>(propertyMappings);

        ListIterator<PropertyMapping<T, ?, K>> iterator = filtered.listIterator();
        while(iterator.hasNext()) {
            PropertyMapping<T, ?, K> pm = iterator.next();
            if (pm.getPropertyMeta().isNonMapped()) {
                iterator.remove();
            } else {
                iterator.set(pm.compressSubSelf());
            }

        }

        return filtered;
    }

    @Override
    public MappingContextFactory<? super S> contextFactory() {
        return mappingContextFactoryBuilder.build();
    }

    private static class MethodFunction implements Function {
        private final Method buildMethod;

        public MethodFunction(Method buildMethod) {
            this.buildMethod = buildMethod;
        }

        @Override
        public Object apply(Object o) {
            try {
                return buildMethod.invoke(o);
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }
    private static class StaticMethodFunction implements Function {
        private final Method buildMethod;

        public StaticMethodFunction(Method buildMethod) {
            this.buildMethod = buildMethod;
        }

        @Override
        public Object apply(Object o) {
            try {
                return buildMethod.invoke(null, o);
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }

    class FieldGenericBuilderInfo {

        final FieldMapper<S, T> targetFieldMapper;
        final FieldMapper<S, GenericBuilder<S, T>> fieldMapperGeneric;
        final Setter<T, GenericBuilder<S, T>> fieldSetter;

        FieldGenericBuilderInfo(FieldMapper<S, T> targetFieldMapper, FieldMapper<S, GenericBuilder<S, T>> fieldMapperGeneric, Setter<T, GenericBuilder<S, T>> fieldSetter) {
            this.targetFieldMapper = targetFieldMapper;
            this.fieldMapperGeneric = fieldMapperGeneric;
            this.fieldSetter = fieldSetter;
        }
    } 
	abstract class FieldMeta {
         abstract FieldMapper<S, T> targetFieldMapper();

        public abstract FieldGenericBuilderInfo fieldGenericBuilderInfo(int index);
    }
    
    class PropertyFieldMeta extends FieldMeta {
        final PropertyMapping<T, ?, K> propertyMapping;

        PropertyFieldMeta(PropertyMapping<T, ?, K> propertyMapping) {
            this.propertyMapping = propertyMapping;
        }
        
        public FieldMapper<S, T> targetFieldMapper() {
            return newFieldMapper(propertyMapping);
        }

        @Override
        public FieldGenericBuilderInfo fieldGenericBuilderInfo(final int index) {

            final Setter setter = propertyMapping.getPropertyMeta().getSetter();
            final ContextualGetter<? super S, ?> getter = fieldMapperFactory.getGetterFromSource(
                    propertyMapping.getColumnKey(),
                    propertyMapping.getPropertyMeta().getPropertyType(),
                    propertyMapping.getColumnDefinition(),
                    propertyMapping.getPropertyMeta().getPropertyClassMetaSupplier(), mappingContextFactoryBuilder);
            return new FieldGenericBuilderInfo(targetFieldMapper(), new FieldMapper<S, GenericBuilder<S, T>>() {
                @Override
                public void mapTo(S source, GenericBuilder<S, T> target, MappingContext<? super S> context) throws Exception {
                    target.objects[index] = getter.get(source, context);
                }
            }, new Setter<T, GenericBuilder<S, T>>() {
                @Override
                public void set(T target, GenericBuilder<S, T> value) throws Exception {
                    setter.set(target, value.objects[index]);
                }
            });
        }
    }

    class SubPropertyFieldMeta extends FieldMeta {
        final SourceMapper<S, ?> mapper;
        final List<PropertyMapping<T, ?, K>> propertyMappings;
        final PropertyMeta<T, ?> owner;
        final MappingContextFactoryBuilder<S, K> currentBuilder;

        SubPropertyFieldMeta(SourceMapper<S, ?> mapper, List<PropertyMapping<T, ?, K>> propertyMappings, PropertyMeta<T, ?> owner, MappingContextFactoryBuilder<S, K> currentBuilder) {
            this.mapper = mapper;
            this.propertyMappings = propertyMappings;
            this.owner = owner;
            this.currentBuilder = currentBuilder;
        }


        @Override
        FieldMapper<S, T> targetFieldMapper() {
            return newMapperFieldMapper(propertyMappings, owner, mapper, currentBuilder);
        }

        @Override
        public FieldGenericBuilderInfo fieldGenericBuilderInfo(final int index) {
            final Setter setter = owner.getSetter();
            return new FieldGenericBuilderInfo(
                    targetFieldMapper(),
                    (FieldMapper<S, GenericBuilder<S, T>>) newMapperFieldMapper(
                            propertyMappings, 
                            new Setter() {
                                @Override
                                public void set(Object target, Object value) throws Exception {
                                    GenericBuilder genericBuilder = (GenericBuilder) target;
                                    genericBuilder.objects[index] = value;
                                    
                                }
                                }, mapper, currentBuilder), 
                    new Setter<T, GenericBuilder<S, T>>() {
                        @Override
                        public void set(T target, GenericBuilder<S, T> value) throws Exception {
                            setter.set(target, value.objects[index]);
                        }
                    });
        }
    }
    
    class FieldMapperFieldMeta extends FieldMeta {
        final FieldMapper<S, T> fieldMapper;

        FieldMapperFieldMeta(FieldMapper<S, T> fieldMapper) {
            this.fieldMapper = fieldMapper;
        }

        @Override
        FieldMapper<S, T> targetFieldMapper() {
            return fieldMapper;
        }

        @Override
        public FieldGenericBuilderInfo fieldGenericBuilderInfo(int index) {
            throw new UnsupportedOperationException();
        }
    }

    private boolean isTargetForMapperFieldMapper(PropertyMapping pm) {
        return
                pm.getPropertyMeta().isSubProperty() || (JoinUtils.isArrayElement(pm.getPropertyMeta()) && isKeyOrHasKey(pm));
    }

    private boolean isKeyOrHasKey(final PropertyMapping pm) {
        if (pm.getColumnDefinition().isInferNull()) return true;

        // looked for non mapped property with same owner
        return propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, K>>() {
            boolean hasKey;
            @Override
            public void handle(PropertyMapping<T, ?, K> tkPropertyMapping) {
                if (tkPropertyMapping.getPropertyMeta().isSubProperty()) {
                    SubPropertyMeta subPropertyMeta = (SubPropertyMeta) tkPropertyMapping.getPropertyMeta();
                    if (subPropertyMeta.getOwnerProperty().equals(pm.getPropertyMeta())) {
                        hasKey |= tkPropertyMapping.getColumnDefinition().isInferNull();
                    }
                }
            }
        }).hasKey;

    }


    private List<PropertyPerOwner> getSubPropertyPerOwner() {

        final List<PropertyPerOwner> subPropertiesList = new ArrayList<PropertyPerOwner>();

        propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,K>>() {

            @SuppressWarnings("unchecked")
            @Override
            public void handle(PropertyMapping<T, ?, K> t) {
                if (t == null) return;
                PropertyMeta<T, ?> meta = t.getPropertyMeta();
                if (meta == null) return;
                if (isTargetForMapperFieldMapper(t)) {
                    addSubProperty(t, meta, t.getColumnKey());
                }
            }
            private <P> void addSubProperty(PropertyMapping<T, ?, K> pm,  PropertyMeta<T,  ?> propertyMeta, K key) {
                PropertyMeta<T, ?> propertyOwner = getOwner(propertyMeta);
                List<PropertyMapping<T, ?, K>> props = getList(propertyOwner);
                if (props == null) {
                    props = new ArrayList<PropertyMapping<T, ?, K>>();
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

            private List<PropertyMapping<T, ?, K>> getList(PropertyMeta<?, ?> owner) {
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
    private <P> SourceMapper<S, P> subPropertyMapper(PropertyMeta<T, P> owner, List<PropertyMapping<T, ?, K>> properties, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {
        final ConstantSourceMapperBuilder<S, P, K> builder =
                newSubBuilder(owner,
                        mappingContextFactoryBuilder,
                        (PropertyFinder<P>) propertyMappingsBuilder.getPropertyFinder().getSubPropertyFinder(owner));


        for(PropertyMapping<T, ?, K> pm : properties) {
            final SubPropertyMeta<T, P,  ?> propertyMeta = (SubPropertyMeta<T, P,  ?>) pm.getPropertyMeta();
            final PropertyMeta<P, ?> subProperty = ((SubPropertyMeta<T, P, ?>) propertyMeta).getSubProperty();
            builder.addMapping(pm.getColumnKey(), pm.getColumnDefinition(), subProperty);
        }
        return builder.sourceFieldMapper();
    }

	@SuppressWarnings("unchecked")
	protected <P> FieldMapper<S, T> newFieldMapper(PropertyMapping<T, P, K> t) {
        ColumnDefinition<K, ?> columnDefinition = t.getColumnDefinition();
        FieldMapper<S, T> fieldMapper = null;
        if (columnDefinition.has(FieldMapperProperty.class)) {
            fieldMapper = (FieldMapper<S, T>) columnDefinition.lookFor(FieldMapperProperty.class).getFieldMapper();
        }
        
		if (fieldMapper == null) {
			fieldMapper = fieldMapperFactory.newFieldMapper(t, mappingContextFactoryBuilder, mapperConfig.mapperBuilderErrorHandler());
		}

        return wrapFieldMapperWithErrorHandler(t.getColumnKey(), fieldMapper);
	}

    private <T> FieldMapper<S, T> wrapFieldMapperWithErrorHandler(final K columnKey, final FieldMapper<S, T> fieldMapper) {
        if (fieldMapper != null && mapperConfig.hasFieldMapperErrorHandler()) {
            return FieldErrorHandlerMapper.<S, T, K>of(columnKey, fieldMapper, mapperConfig.fieldMapperErrorHandler());
        }
        return fieldMapper;
    }

    private <T> ContextualGetter<S, T> wrapGetterWithErrorHandler(final K columnKey, final ContextualGetter<S, T> getter) {
        if (getter != null && mapperConfig.hasFieldMapperErrorHandler()) {
            return FieldErrorHandlerGetter.<S, T, K>of(columnKey, getter, mapperConfig.fieldMapperErrorHandler());
        }
        return getter;
    }

    @Override
    public void addMapper(FieldMapper<S, T> mapper) {
		additionalMappers.add(mapper);
	}



    private <ST> ConstantSourceMapperBuilder<S, ST, K> newSubBuilder(
            PropertyMeta<?, ST> propertyMeta,
            MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder,
            PropertyFinder<ST> propertyFinder) {
        return ConstantSourceMapperBuilder.<S, ST, K>newConstantSourceMapperBuilder(
                mapperSource,
                propertyMeta,
                mapperConfig,
                mappingContextFactoryBuilder,
                keyFactory,
                propertyFinder);
    }

    @Override
    public List<K> getKeys() {
        return propertyMappingsBuilder.getKeys();
    }

    @Override
    public <H extends ForEachCallBack<PropertyMapping<T, ?, K>>> H forEachProperties(H handler) {
        return propertyMappingsBuilder.forEachProperties(handler);
    }
    @SuppressWarnings("unchecked")
    private List<KeyAndPredicate<S, K>> getSubKeys(List<PropertyMapping<T, ?, K>> properties) {
        List<KeyAndPredicate<S, K>> keys = new ArrayList<KeyAndPredicate<S, K>>();

        // look for keys property of the object
        for (PropertyMapping<T, ?, K> pm : properties) {
            Predicate<PropertyMeta<?, ?>> propertyMetaKeyPredicate = pm.getColumnDefinition().keyAppliesTo();
            if (pm.getPropertyMeta().isSubProperty()) {
                SubPropertyMeta<T, ?, ?> subPropertyMeta = (SubPropertyMeta<T, ?, ?>) pm.getPropertyMeta();
                if (!(JoinUtils.isArrayElement(subPropertyMeta.getSubProperty()))) {
                    // ignore ArrayElementPropertyMeta as it's a direct getter and will be managed in the setter
                    if (pm.getColumnDefinition().isKey()) {
                        if (propertyMetaKeyPredicate.test(subPropertyMeta.getSubProperty())) {
                            Predicate<? super S> predicate = buildKeyPredicate(subPropertyMeta.getSubProperty(), propertyMetaKeyPredicate);;
                            keys.add(new KeyAndPredicate<S, K>(pm.getColumnKey(), predicate));
                        }
                    }
                }
            } else {
                if (pm.getColumnDefinition().isKey()) {
                    if (propertyMetaKeyPredicate.test(pm.getPropertyMeta())) {
                        keys.add(new KeyAndPredicate<S, K>(pm.getColumnKey(), null));
                    }
                }
            }
        }

        return keys;
    }

    private List<KeyAndPredicate<S, K>> getInferNulls(List<PropertyMapping<T, ?, K>> properties) {
        List<KeyAndPredicate<S, K>> keys = new ArrayList<KeyAndPredicate<S, K>>();

        // look for keys property of the object
        for (PropertyMapping<T, ?, K> pm : properties) {
            Predicate<PropertyMeta<?, ?>> propertyMetaKeyPredicate = pm.getColumnDefinition().inferNullsAppliesTo();
            if (pm.getPropertyMeta().isSubProperty()) {
                SubPropertyMeta<T, ?, ?> subPropertyMeta = (SubPropertyMeta<T, ?, ?>) pm.getPropertyMeta();
                if (!(JoinUtils.isArrayElement(subPropertyMeta.getSubProperty()))) {
                    // ignore ArrayElementPropertyMeta as it's a direct getter and will be managed in the setter
                    if (pm.getColumnDefinition().isInferNull()) {
                        if (propertyMetaKeyPredicate.test(subPropertyMeta.getSubProperty())) {
                            Predicate<? super S> predicate = buildKeyPredicate(subPropertyMeta.getSubProperty(), propertyMetaKeyPredicate);;
                            keys.add(new KeyAndPredicate<S, K>(pm.getColumnKey(), predicate));
                        }
                    }
                }
            } else {
                if (pm.getColumnDefinition().isInferNull()) {
                    if (propertyMetaKeyPredicate.test(pm.getPropertyMeta())) {
                        keys.add(new KeyAndPredicate<S, K>(pm.getColumnKey(), null));
                    }
                }
            }
        }

        return keys;
    }

    private Predicate<S> buildKeyPredicate(final PropertyMeta<?, ?> propertyMeta, final Predicate<PropertyMeta<?, ?>> propertyMetaPredicate) {
        Predicate<S> predicate = null;
        if (propertyMeta instanceof DiscriminatorPropertyFinder.DiscriminatorPropertyMeta) {
            
            final DiscriminatorPropertyFinder.DiscriminatorPropertyMeta<?, ?> dpm = (DiscriminatorPropertyFinder.DiscriminatorPropertyMeta<?, ?>) propertyMeta;

            final List<Predicate<? super S>> not =
                dpm.forEachProperty(new Consumer<DiscriminatorPropertyFinder.DiscriminatorMatch>() {
                    List<Predicate<? super S>> not = new ArrayList<Predicate<? super S>>();
                    @Override
                    public void accept(DiscriminatorPropertyFinder.DiscriminatorMatch dm) {

                        Type type = dm.type;
                        PropertyMeta<?, ?> propertyMeta = dm.matchedProperty.getPropertyMeta();

                        if (!propertyMetaPredicate.test(propertyMeta)) {
                            MapperConfig.Discriminator<Object, K, Object>[] discriminators = mapperConfig.getDiscriminators(dpm.getOwnerType());
                            for (MapperConfig.Discriminator<Object, K, Object> discriminator : discriminators) {
                                if (MapperConfig.sameDiscriminatorId(dm.discriminatorId, discriminator.discriminatorId)) {
                                    not.add(discriminator.getCase(type).predicateFactory.apply(findAllDiscriminatorKeys(discriminator.discriminatorId)));
                                }
                            }
                        } else {
                            Predicate<? super S> p = buildKeyPredicate(propertyMeta, propertyMetaPredicate);
                            
                            if (p != null) {
                                not.add(p);
                            }
                        }
                    }
                }).not;
            
            if (not.isEmpty()) return null;
            
            return new DiscriminatorKeyPredicate<S>(not);
                    
        } else if (propertyMeta.isSubProperty()) {
            SubPropertyMeta subPropertyMeta = (SubPropertyMeta) propertyMeta;
            predicate = buildKeyPredicate(subPropertyMeta.getSubProperty(), propertyMetaPredicate);
        }
        return predicate;
    }

    public static class TargetFromBuilderParamBiFunction implements BiFunction<Object[], Object, Object> {
        private final int builderIndex;

        public TargetFromBuilderParamBiFunction(int builderIndex) {
            this.builderIndex = builderIndex;
        }

        @Override
        public Object apply(Object[] objects, Object o) {
            return objects[builderIndex];
        }
    }

    public static class TargetFromBuilderWithTransformBiFunction implements BiFunction<Object[], Object, Object> {
        private final Function transformer;
        private final int builderIndex;

        public TargetFromBuilderWithTransformBiFunction(Function transformer, int builderIndex) {
            this.transformer = transformer;
            this.builderIndex = builderIndex;
        }

        @Override
        public Object apply(Object[] objects, Object o) {
            return transformer.apply(objects[builderIndex]);
        }
    }

    public static class InstantiatorAndFieldMappers<S, T> {
        public final ConstructorInjections<S, T> constructorInjections;
        public final BiInstantiator<S, MappingContext<? super S>, T> instantiator;

        public InstantiatorAndFieldMappers(ConstructorInjections constructorInjections,  BiInstantiator<S, MappingContext<? super S>, T> instantiator) {
            this.constructorInjections = constructorInjections;
            this.instantiator = instantiator;
        }
    }
    public static class ConstructorInjections<S, T> {
        private final Map<Parameter, ContextualGetter<? super S, ?>> parameterGetterMap;
        private final FieldMapper<S, T>[] fieldMappers;

        public ConstructorInjections(Map<Parameter, ContextualGetter<? super S, ?>> parameterGetterMap, FieldMapper<S, T>[] fieldMappers) {
            this.parameterGetterMap = parameterGetterMap;
            this.fieldMappers = fieldMappers;
        }
    }
    
    public static abstract class InjectionParam<T> {
        final Parameter parameter;
        final PropertyMeta<T, ?> propertyMeta;

        private InjectionParam(Parameter parameter, PropertyMeta<T, ?> propertyMeta) {
            this.parameter = parameter;
            this.propertyMeta = propertyMeta;
        }
        
        abstract GetterAndFieldMapper getterAndfieldMapper();

        abstract GenericBuilderGetterAndFieldMapper getterAndfieldMapperGenericBuilder(int i);

        public abstract boolean needTransformer();

        boolean needTransformer(PropertyMeta<T, ?> propertyMeta) {
            if (propertyMeta.isNonMapped()) return false;
            if (propertyMeta.isSubProperty()) {
                SubPropertyMeta sb = (SubPropertyMeta) propertyMeta;
                return needTransformer(sb.getOwnerProperty()) || needTransformer(sb.getSubProperty());
            } else {
                return !propertyMeta.isSelf() && propertyMeta.getPropertyClassMeta().needTransformer();
            }
        }

    }
    
    private class ConstructorParam extends InjectionParam {
        private final PropertyMapping<T, ?, K> propertyMapping;
        
        private ConstructorParam(Parameter parameter, ConstructorPropertyMeta<T, ?> propertyMeta, PropertyMapping<T, ?, K> propertyMapping) {
            super(parameter, propertyMeta);
            this.propertyMapping = propertyMapping;
        }
        
        public GetterAndFieldMapper getterAndfieldMapper() {
            ContextualGetter<? super S, ?> getter = wrapGetterWithErrorHandler(propertyMapping.getColumnKey(), (ContextualGetter)getGetter());
            FieldMapper<S, T> fieldMapper;
            if (NullSetter.isNull(propertyMeta.getSetter())) {
                fieldMapper = null;
            } else {
                fieldMapper = wrapFieldMapperWithErrorHandler(propertyMapping.getColumnKey(), fieldMapperFactory.newFieldMapper(propertyMapping, mappingContextFactoryBuilder, mapperConfig.mapperBuilderErrorHandler()));
            }
            return new GetterAndFieldMapper((ContextualGetter<S, Object>) getter, fieldMapper);
        }

        private ContextualGetter<? super S, ?> getGetter() {
            ContextualGetter<? super S, ?> getter =
                    fieldMapperFactory.getGetterFromSource(propertyMapping.getColumnKey(), propertyMeta.getPropertyType(), propertyMapping.getColumnDefinition(), propertyMeta.getPropertyClassMetaSupplier(), mappingContextFactoryBuilder);
            if (NullContextualGetter.isNull(getter)) {
                PropertyMapping<T, ?, K> propertyMapping = this.propertyMapping;
                mapperConfig.mapperBuilderErrorHandler()
                        .accessorNotFound(getterNotFoundErrorMessage(propertyMapping));
            }
            return getter;
        }

        @Override
        GenericBuilderGetterAndFieldMapper getterAndfieldMapperGenericBuilder(final int index) {
            final ContextualGetter<? super S, ?> getter = getGetter();

            if (NullContextualGetter.isNull(getter)) {
                PropertyMapping<T, ?, K> propertyMapping = this.propertyMapping;
                mapperConfig.mapperBuilderErrorHandler()
                        .accessorNotFound(getterNotFoundErrorMessage(propertyMapping));
                return null;
            }
            
            FieldMapper<S, GenericBuilder<S, T>> fieldMapper =  wrapFieldMapperWithErrorHandler(propertyMapping.getColumnKey(), new GenericBuilderFieldMapper<S, T>(index, getter));
            FieldMapper<S, T> fieldMapperAfterConstruct =
                    NullSetter.isNull(propertyMeta.getSetter()) ? 
                        null : 
                        wrapFieldMapperWithErrorHandler(propertyMapping.getColumnKey(), fieldMapperFactory.newFieldMapper(propertyMapping, mappingContextFactoryBuilder, mapperConfig.mapperBuilderErrorHandler()));

            return new GenericBuilderGetterAndFieldMapper((ContextualGetter<S, Object>) getter, fieldMapper, null, fieldMapperAfterConstruct);

        }

        @Override
        public boolean needTransformer() {
            return needTransformer(propertyMeta);
        }



    }

    private static class GenericBuilderFieldMapper<S, T> implements FieldMapper<S, GenericBuilder<S, T>> {
        private final int index;
        private final ContextualGetter<? super S, ?> getter;

        public GenericBuilderFieldMapper(int index, ContextualGetter<? super S, ?> getter) {
            this.index = index;
            this.getter = getter;
        }

        @Override
        public void mapTo(S source, GenericBuilder<S, T> target, MappingContext<? super S> context) throws Exception {
            target.objects[index] = getter.get(source, context);
        }
    }

    private class ContextParam extends InjectionParam {

        private ContextParam(Parameter parameter, ConstructorPropertyMeta<T, ?> propertyMeta) {
            super(parameter, propertyMeta);
        }

        public GetterAndFieldMapper getterAndfieldMapper() {
            ContextualGetter<? super S, ?> getter = getGetter();
            return new GetterAndFieldMapper((ContextualGetter<S, Object>) getter, null);
        }

        private ContextualGetter<S, Object> getGetter() {
            return new ContextualGetter<S, Object>() {
                @Override
                public Object get(S s, Context context) throws Exception {
                    return context;
                }
            };
        }

        @Override
        GenericBuilderGetterAndFieldMapper getterAndfieldMapperGenericBuilder(final int index) {
            final ContextualGetter<? super S, ?> getter = getGetter();

            FieldMapper<S, GenericBuilder<S, T>> fieldMapper =  new GenericBuilderFieldMapper<S, T>(index, getter);

            return new GenericBuilderGetterAndFieldMapper((ContextualGetter<S, Object>) getter, fieldMapper, null, null);

        }

        @Override
        public boolean needTransformer() {
            return false;
        }

    }

    public static class SubPropertyParam<S, T, K extends FieldKey<K>> extends InjectionParam<T> {
        final List<PropertyMapping<T, ?, K>> propertyMappings;
        final DefaultConstantSourceMapperBuilder<S, T, K> builder;

        private SubPropertyParam(Parameter parameter, ConstructorPropertyMeta<T, ?> propertyMeta, List<PropertyMapping<T, ?, K>> propertyMappings, DefaultConstantSourceMapperBuilder<S, T, K> builder) {
            super(parameter, propertyMeta);
            this.propertyMappings = propertyMappings;
            this.builder = builder;
        }

        public GetterAndFieldMapper getterAndfieldMapper() {

            final MappingContextFactoryBuilder currentBuilder = builder.getMapperContextFactoryBuilder(propertyMeta, propertyMappings);

            final SourceMapper<S, ?> mapper = getsSourceMapper(currentBuilder);
            ContextualGetter<S, ?> biFunction = builder.newMapperGetterAdapter(mapper, currentBuilder);
            FieldMapper<S, T> fieldMapper = builder.newMapperFieldMapper(propertyMappings, propertyMeta, mapper, currentBuilder);
            return new GetterAndFieldMapper(biFunction, fieldMapper);
        }

        @Override
        GenericBuilderGetterAndFieldMapper getterAndfieldMapperGenericBuilder(final int i) {
            final MappingContextFactoryBuilder currentBuilder = builder.getMapperContextFactoryBuilder(propertyMeta, propertyMappings);

            SourceMapper<S, ?> mapper = getsSourceMapper(currentBuilder);
            Function<?, T> transform = null;

            if (mapper instanceof TransformSourceFieldMapper) {
                transform = ((TransformSourceFieldMapper) mapper).transform;
                mapper = ((TransformSourceFieldMapper) mapper).delegate;
            }
            
            ContextualGetter<S, Object> biFunction = builder.newMapperGetterAdapter(mapper, currentBuilder);
            
            
            FieldMapper<S, GenericBuilder<S, T>> fieldMapper = builder.newMapperFieldMapper(propertyMappings, new GenericBuilderSetter(i), mapper , currentBuilder);
            FieldMapper<S, T> fieldMapperAfterConstruct = builder.newMapperFieldMapper(propertyMappings, propertyMeta, mapper, currentBuilder);

            return new GenericBuilderGetterAndFieldMapper(biFunction, fieldMapper, transform, fieldMapperAfterConstruct);
        }

        @Override
        public boolean needTransformer() {
            if (needTransformer(propertyMeta)) {
                return true;   
            }
            
            for(PropertyMapping pm : propertyMappings) {
                if (needTransformer(pm.getPropertyMeta())) return true;
            }
            return false;
        }

        private SourceMapper<S, ?> getsSourceMapper(MappingContextFactoryBuilder currentBuilder) {
            final SourceMapper<S, ?> mapper;
            if (propertyMappings.size() == 1 && JoinUtils.isArrayElement(propertyMappings.get(0).getPropertyMeta())) {
                mapper = builder.getterPropertyMapper(propertyMeta, propertyMappings.get(0));
            } else {
                mapper = builder.subPropertyMapper(propertyMeta, propertyMappings, currentBuilder);
            }
            return mapper;
        }

        private class GenericBuilderSetter<S, T> implements Setter<GenericBuilder<S, T>, Object> {
            private final int index;

            public GenericBuilderSetter(int index) {
                this.index = index;
            }

            @Override
            public void set(GenericBuilder<S, T> target, Object value) throws Exception {
                target.objects[index] = value;
            }
        }
    }
    
    public static class GetterAndFieldMapper<S, T> {
        final ContextualGetter<S, ?> getter;
        final FieldMapper<S, T> fieldMapper;

        private GetterAndFieldMapper(ContextualGetter<S, ?> getter, FieldMapper<S, T> fieldMapper) {
            this.getter = getter;
            this.fieldMapper = fieldMapper;
        }
    }

    public static class GenericBuilderGetterAndFieldMapper<S, T> {
        final ContextualGetter<S, Object> getter;
        final FieldMapper<S, T> fieldMapper;
        final Function<?, T> transform;
        final FieldMapper<S, T> fieldMapperAfterConstruct;

        private GenericBuilderGetterAndFieldMapper(ContextualGetter<S, Object> getter, FieldMapper<S, T> fieldMapper, Function<?, T> transform, FieldMapper<S, T> fieldMapperAfterConstruct) {
            this.getter = getter;
            this.fieldMapper = fieldMapper;
            this.transform = transform;
            this.fieldMapperAfterConstruct = fieldMapperAfterConstruct;
        }
    }

    private class PropertyPerOwner {
        private final PropertyMeta<T, ?> owner;
        private final List<PropertyMapping<T, ?, K>> propertyMappings;

        private PropertyPerOwner(PropertyMeta<T, ?> owner, List<PropertyMapping<T, ?, K>> propertyMappings) {
            this.owner = owner;
            this.propertyMappings = propertyMappings;
        }
    }

    private class FieldMapperFactoryGetterFactoryAdapter implements ContextualGetterFactory<S, K> {
        private final FieldMapperErrorHandler<? super K> fieldMapperErrorHandler;

        public FieldMapperFactoryGetterFactoryAdapter(FieldMapperErrorHandler<? super K> fieldMapperErrorHandler) {
            this.fieldMapperErrorHandler = fieldMapperErrorHandler;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <P> ContextualGetter<S, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
            FieldMapperColumnDefinition<K> columnDefinition = FieldMapperColumnDefinition.<K>identity().add(properties);
            ContextualGetter<? super S, ? extends P> getterFromSource = fieldMapperFactory.getGetterFromSource(key, target, columnDefinition, new ClassMetaSupplier<P>(target), mappingContextFactoryBuilder);
            
            if (fieldMapperErrorHandler != null) {
                return FieldErrorHandlerGetter.<S, P, K>of(key, getterFromSource, fieldMapperErrorHandler);
            }
            return (ContextualGetter<S, P>) getterFromSource;
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
    public static String getterNotFoundErrorMessage(PropertyMapping propertyMapping) {
        String currentPath = propertyMapping.getPropertyMeta().getPath();
        return "Could not find getter for " + propertyMapping.getColumnKey() + " type "
                + propertyMapping.getPropertyMeta().getPropertyType()
                + " path " + currentPath
                + ". See " + CSFM_GETTER_NOT_FOUND.toUrl();
    }

    private static class DiscriminatorKeyPredicate<S> implements Predicate<S> {
        private final List<Predicate<? super S>> not;

        public DiscriminatorKeyPredicate(List<Predicate<? super S>> not) {
            this.not = not;
        }

        @Override
        public boolean test(S s) {
            for(Predicate<? super S> p : not) {
                if (p.test(s)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DiscriminatorKeyPredicate<?> that = (DiscriminatorKeyPredicate<?>) o;

            return not != null ? not.equals(that.not) : that.not == null;
        }

        @Override
        public int hashCode() {
            return not != null ? not.hashCode() : 0;
        }
    }
}