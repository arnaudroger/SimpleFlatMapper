package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.asm.MapperAsmFactory;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.fieldmapper.MapperFieldMapper;
import org.simpleflatmapper.map.getter.NullContextualGetter;
import org.simpleflatmapper.map.impl.FieldErrorHandlerGetter;
import org.simpleflatmapper.map.impl.GenericBuilder;
import org.simpleflatmapper.map.impl.GetterMapper;
import org.simpleflatmapper.map.impl.JoinUtils;
import org.simpleflatmapper.map.property.DefaultValueProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.FieldMapperProperty;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.impl.FieldErrorHandlerMapper;
import org.simpleflatmapper.map.fieldmapper.ConstantSourceFieldMapperFactory;
import org.simpleflatmapper.map.fieldmapper.ConstantSourceFieldMapperFactoryImpl;
import org.simpleflatmapper.map.property.MandatoryProperty;
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
import org.simpleflatmapper.reflect.meta.ArrayClassMeta;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ConstructorPropertyMeta;
import org.simpleflatmapper.reflect.meta.ObjectClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SelfPropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.Named;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

import static org.simpleflatmapper.util.Asserts.requireNonNull;
import static org.simpleflatmapper.util.ErrorDoc.CSFM_GETTER_NOT_FOUND;

public final class ConstantSourceMapperBuilder<S, T, K extends FieldKey<K>>  {

    private static final FieldKey[] FIELD_KEYS = new FieldKey[0];
    public static final FieldMapper[] EMPTY_FIELD_MAPPERS = new FieldMapper[0];

    private final Type target;

	private final ConstantSourceFieldMapperFactory<S, K> fieldMapperFactory;

	protected final PropertyMappingsBuilder<T, K> propertyMappingsBuilder;
	protected final ReflectionService reflectionService;

	private final List<FieldMapper<S, T>> additionalMappers = new ArrayList<FieldMapper<S, T>>();

    private final MapperSource<? super S, K> mapperSource;
    private final MapperConfig<K> mapperConfig;
    protected final MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder;

    private final KeyFactory<K> keyFactory;


    public ConstantSourceMapperBuilder(
            final MapperSource<? super S, K> mapperSource,
            final ClassMeta<T> classMeta,
            final MapperConfig<K> mapperConfig,
            MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder,
            KeyFactory<K> keyFactory) throws MapperBuildingException {
                this(mapperSource, classMeta, mapperConfig, mappingContextFactoryBuilder, keyFactory, null);
    }

    public ConstantSourceMapperBuilder(
            final MapperSource<? super S, K> mapperSource,
            final ClassMeta<T> classMeta,
            final MapperConfig<K> mapperConfig,
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
                    mappingContextFactoryBuilder.addKey(key);
                }
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public ContextualSourceFieldMapperImpl<S, T> mapper() {
        SourceFieldMapper<S, T> mapper = sourceFieldMapper();
        return new ContextualSourceFieldMapperImpl<S, T>(mappingContextFactoryBuilder.build(), mapper);

    }

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
            ConstructorInjections<T> constructorInjections = toConstructorInjections(injectionParams);
            InstantiatorAndFieldMappers<T> constructorFieldMappersAndInstantiator = getConstructorFieldMappersAndInstantiator(constructorInjections);
            mapper = buildMapper(targetFieldMappers(), constructorFieldMappersAndInstantiator, getTargetClass());
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
            return buildWithGenericBuilder(injections);
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

    private SourceFieldMapper<S, T> buildWithGenericBuilder(List<InjectionParam> params) {


        List<FieldMeta> fields = fields();
        
        final Class<?> targetClass = GenericBuilder.class;

        int nbParams = params.size();
        final ContextualGetter[] biFunctions = new ContextualGetter[nbParams];
        final Parameter[] indexMapping = new Parameter[nbParams];
        final Function[] transformers = new Function[nbParams];


        Map<Parameter, ContextualGetter<? super S,  ?>> parameterGetterMap = new HashMap<Parameter, ContextualGetter<? super S, ?>>();
        List<FieldMapper<S, T>> constructorFieldMapperGeneric = new ArrayList<FieldMapper<S, T>>();
        List<FieldMapper<S, T>> constructFieldMapperTarget = new ArrayList<FieldMapper<S, T>>();
        List<FieldMapper<S, T>> targetFieldMappers = new ArrayList<FieldMapper<S, T>>();
        List<FieldMapper<S, GenericBuilder<T>>> fieldMapperGeneric = new ArrayList<FieldMapper<S, GenericBuilder<T>>>();
        List<Setter<T, GenericBuilder<T>>> fieldSetters = new ArrayList<Setter<T, GenericBuilder<T>>>();


 
        


        int i = 0;
        for(InjectionParam p : params) {
            GenericBuilderGetterAndFieldMapper getterAndFieldMapper = p.getterAndfieldMapperGenericBuilder(i);
            
            parameterGetterMap.put(p.parameter, getterAndFieldMapper.getter);
            if (getterAndFieldMapper.fieldMapper != null) {
                constructorFieldMapperGeneric.add(getterAndFieldMapper.fieldMapper);
                constructFieldMapperTarget.add(getterAndFieldMapper.fieldMapperAfterConstruct);
            }
            
            ContextualGetter<? super S, ?> biFunction = getterAndFieldMapper.getter;
            
            biFunctions[i] = biFunction;
            indexMapping[i] = p.parameter;
            transformers[i] = getterAndFieldMapper.transform;
            i++;
        }

        

        for(FieldMeta fm : fields) {
            FieldGenericBuilderInfo fieldGenericBuilderInfo = fm.fieldGenericBuilderInfo(i);
            targetFieldMappers.add(fieldGenericBuilderInfo.targetFieldMapper);
            fieldMapperGeneric.add(fieldGenericBuilderInfo.fieldMapperGeneric);
            fieldSetters.add(fieldGenericBuilderInfo.fieldSetter);
        }


        final Function<GenericBuilder<T>, T> transformFunction = new GenericBuilderTransformFunction<T>(fieldSetters.toArray(new Setter[0]));

        ConstructorInjections<T> constructorInjections = new ConstructorInjections<T>(parameterGetterMap, constructorFieldMapperGeneric.toArray(new FieldMapper[0]));

        final BiInstantiator<Object[], Object, Object> targetInstantiatorFromGenericBuilder = targetInstantiatorFromGenericBuilder(indexMapping, transformers);

        BiInstantiator genericBuilderInstantiator = new GenericBuildBiInstantiator(biFunctions, targetInstantiatorFromGenericBuilder, fields.size());

        InstantiatorAndFieldMappers newConstantSourceMapperBuilder =
                new InstantiatorAndFieldMappers(
                        constructorInjections,
                        genericBuilderInstantiator);

        SourceFieldMapper<S, GenericBuilder<T>> delegate = buildMapper(fieldMapperGeneric.toArray(EMPTY_FIELD_MAPPERS), newConstantSourceMapperBuilder, targetClass);

        return new TransformSourceFieldMapper<S, GenericBuilder<T>, T>(delegate, merge(constructFieldMapperTarget.toArray(EMPTY_FIELD_MAPPERS), targetFieldMappers.toArray(EMPTY_FIELD_MAPPERS)), transformFunction);

    }
    private FieldMapper<S, T>[] merge(FieldMapper<S, T>[] fieldMappers, FieldMapper<S, T>[] fields) {
        FieldMapper<S, T>[] f = new FieldMapper[fieldMappers.length + fields.length];

        System.arraycopy(fieldMappers, 0, f, 0, fieldMappers.length);
        System.arraycopy(fields, 0, f, fieldMappers.length, fields.length);

        return f;
    }

    private BiInstantiator<Object[], Object, Object> targetInstantiatorFromGenericBuilder(Parameter[] indexMapping, Function[] transformers) {
        InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();


        Map<Parameter, BiFunction<? super Object[], ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Object[], ? super Object, ?>>();

        for(int i = 0; i < indexMapping.length; i++) {
            Parameter parameter = indexMapping[i];
            final int builderIndex = i;
            final Function transformer = transformers[i];
            if (transformer == null) {
                params.put(parameter, new TargetFromBuilderParamBiFunction(builderIndex));
            } else {
                params.put(parameter, new TargetFromBuilderWithTransformBiFunction(transformer, builderIndex));
            }
        }
        BiInstantiator<Object[], Object, Object> targetInstantiator = instantiatorFactory.getBiInstantiator(getTargetClass(), Object[].class, Object.class,
                propertyMappingsBuilder.getPropertyFinder().getEligibleInstantiatorDefinitions(), params, reflectionService.isAsmActivated(), reflectionService.builderIgnoresNullValues());
        
        return targetInstantiator;
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
        SourceFieldMapper delegate = buildMapper(fields, newConstantSourceMapperBuilder, targetClass);
        return new TransformSourceFieldMapper<S, Object, T>(delegate, fields, f);
    }

    private <T> SourceFieldMapper<S, T> buildMapper(FieldMapper<S, T>[] fields, InstantiatorAndFieldMappers<T> constructorFieldMappersAndInstantiator, Class<T> target) {
        SourceFieldMapper<S, T> mapper;

        if (isEligibleForAsmMapper()) {
            try {
                MapperAsmFactory mapperAsmFactory = reflectionService
                        .getAsmFactory()
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
                                        getKeys(),
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

    public boolean isRootAggregate() {
        return mappingContextFactoryBuilder.isRoot()
                && !mappingContextFactoryBuilder.hasNoDependentKeys();
    }
	private Class<T> getTargetClass() {
		return TypeHelper.toClass(target);
	}


    private <T> ConstructorInjections<T> toConstructorInjections(List<InjectionParam> params) throws MapperBuildingException {

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

        return new ConstructorInjections<T>(injections, fieldMappers.toArray(new FieldMapper[0]));
    }
	
	@SuppressWarnings("unchecked")
    private InstantiatorAndFieldMappers<T> getConstructorFieldMappersAndInstantiator(ConstructorInjections<T> constructorInjections) throws MapperBuildingException {
 
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
		propertyMappingsBuilder.forEachConstructorProperties(new ForEachCallBack<PropertyMapping<T,?,K>>() {
            @SuppressWarnings("unchecked")
			@Override
			public void handle(PropertyMapping<T, ?, K> propertyMapping) {
                if (!isTargetForMapperFieldMapper(propertyMapping)) {
                    PropertyMeta<T, ?> pm = propertyMapping.getPropertyMeta();
                    ConstructorPropertyMeta<T, ?> cProp = (ConstructorPropertyMeta<T, ?>) pm;
                    injectionParams.add(new ConstructorParam(cProp.getParameter(), cProp, propertyMapping));
                }
			}
		});

        for(PropertyPerOwner e :
                getSubPropertyPerOwner()) {
            if (e.owner.isConstructorProperty()) {
                ConstructorPropertyMeta<T, ?> meta = (ConstructorPropertyMeta<T, ?>) e.owner;
                injectionParams.add(new SubPropertyParam(meta.getParameter(), meta, e.propertyMappings));
            }
        }
        
        return injectionParams;
	}

    private <P> SourceMapper<S, P> getterPropertyMapper(PropertyMeta<T, P> owner, PropertyMapping<T, ?, K> propertyMapping) {
        PropertyMeta<T, ?> pm = propertyMapping.getPropertyMeta();
        final ContextualGetter<? super S, P> getter =
                (ContextualGetter<? super S, P>) fieldMapperFactory.getGetterFromSource(propertyMapping.getColumnKey(), pm.getPropertyType(), propertyMapping.getColumnDefinition(), pm.getPropertyClassMetaSupplier(), mappingContextFactoryBuilder);

        return new GetterMapper<S, P>(getter);
    }

    private MappingContextFactoryBuilder getMapperContextFactoryBuilder(PropertyMeta<?, ?> owner, List<PropertyMapping<T, ?, K>> properties) {
        final List<K> subKeys = getSubKeys(properties);
        return mappingContextFactoryBuilder.newBuilder(subKeys, owner);
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
    private <P> void addMapping(K columnKey, ColumnDefinition<K, ?> columnDefinition,  PropertyMeta<T, P> prop) {
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
                 if (!meta.isConstructorProperty() && !isTargetForMapperFieldMapper(t)) {
					fields.add(new PropertyFieldMeta(t));
				}
			}
		});

        for(PropertyPerOwner e :
                getSubPropertyPerOwner()) {
            if (!e.owner.isConstructorProperty()) {
                final MappingContextFactoryBuilder currentBuilder = getMapperContextFactoryBuilder(e.owner, e.propertyMappings);

                final SourceMapper<S, ?> mapper;
                if (e.propertyMappings.size() == 1 && JoinUtils.isArrayElement(e.propertyMappings.get(0).getPropertyMeta())) {
                    mapper = getterPropertyMapper(e.owner, e.propertyMappings.get(0));
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
        final FieldMapper<S, GenericBuilder<T>> fieldMapperGeneric;
        final Setter<T, GenericBuilder<T>> fieldSetter;

        FieldGenericBuilderInfo(FieldMapper<S, T> targetFieldMapper, FieldMapper<S, GenericBuilder<T>> fieldMapperGeneric, Setter<T, GenericBuilder<T>> fieldSetter) {
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
            return new FieldGenericBuilderInfo(targetFieldMapper(), new FieldMapper<S, GenericBuilder<T>>() {
                @Override
                public void mapTo(S source, GenericBuilder<T> target, MappingContext<? super S> context) throws Exception {
                    target.objects[index] = getter.get(source, context);
                }
            }, new Setter<T, GenericBuilder<T>>() {
                @Override
                public void set(T target, GenericBuilder<T> value) throws Exception {
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
                    (FieldMapper<S, GenericBuilder<T>>) newMapperFieldMapper(
                            propertyMappings, 
                            new Setter() {
                                @Override
                                public void set(Object target, Object value) throws Exception {
                                    GenericBuilder genericBuilder = (GenericBuilder) target;
                                    genericBuilder.objects[index] = value;
                                    
                                }
                                }, mapper, currentBuilder), 
                    new Setter<T, GenericBuilder<T>>() {
                        @Override
                        public void set(T target, GenericBuilder<T> value) throws Exception {
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
        return pm.getPropertyMeta().isSubProperty() || (JoinUtils.isArrayElement(pm.getPropertyMeta()) && pm.getColumnDefinition().isKey());
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
                newSubBuilder(owner.getPropertyClassMeta(),
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
    private List<K> getSubKeys(List<PropertyMapping<T, ?, K>> properties) {
        List<K> keys = new ArrayList<K>();

        // look for keys property of the object
        for (PropertyMapping<T, ?, K> pm : properties) {
            
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

    private static class GenericBuilderTransformFunction<T> implements Function<GenericBuilder<T>, T> {
        private final Setter<T, GenericBuilder<T>>[] fieldSetters;

        public GenericBuilderTransformFunction(Setter<T, GenericBuilder<T>>[] fieldSetters) {
            this.fieldSetters = fieldSetters;   
        }

        @Override
        public T apply(GenericBuilder<T> o) {
            try {
                if (o == null) return null;
                T t = o.build();
                for(Setter<T, GenericBuilder<T>> setter : fieldSetters) {
                    setter.set(t, o);
                }
                return t;
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }

    private static class GenericBuildBiInstantiator implements BiInstantiator {
        private final ContextualGetter[] biFunctions;
        private final BiInstantiator<Object[], Object, Object> targetInstantiatorFromGenericBuilder;
        private final int nbFields;

        public GenericBuildBiInstantiator(ContextualGetter[] biFunctions, BiInstantiator<Object[], Object, Object> targetInstantiatorFromGenericBuilder, int nbFields) {
            this.biFunctions = biFunctions;
            this.targetInstantiatorFromGenericBuilder = targetInstantiatorFromGenericBuilder;
            this.nbFields = nbFields;
        }

        @Override
        public Object newInstance(Object o, Object o2) throws Exception {
            GenericBuilder genericBuilder = new GenericBuilder(biFunctions.length + nbFields, targetInstantiatorFromGenericBuilder);

            for (int i = 0; i < biFunctions.length; i++) {
                genericBuilder.objects[i] = biFunctions[i].get(o, (MappingContext<?>) o2);
            }
            return genericBuilder;
        }
    }

    private static class TargetFromBuilderParamBiFunction implements BiFunction<Object[], Object, Object> {
        private final int builderIndex;

        public TargetFromBuilderParamBiFunction(int builderIndex) {
            this.builderIndex = builderIndex;
        }

        @Override
        public Object apply(Object[] objects, Object o) {
            return objects[builderIndex];
        }
    }

    private static class TargetFromBuilderWithTransformBiFunction implements BiFunction<Object[], Object, Object> {
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

    private class InstantiatorAndFieldMappers<T> {
        private final ConstructorInjections<T> constructorInjections;
        private final BiInstantiator<S, MappingContext<? super S>, T> instantiator;

        private InstantiatorAndFieldMappers(ConstructorInjections constructorInjections,  BiInstantiator<S, MappingContext<? super S>, T> instantiator) {
            this.constructorInjections = constructorInjections;
            this.instantiator = instantiator;
        }
    }
    private class ConstructorInjections<T> {
        private final Map<Parameter, ContextualGetter<? super S, ?>> parameterGetterMap;
        private final FieldMapper<S, T>[] fieldMappers;

        private ConstructorInjections(Map<Parameter, ContextualGetter<? super S, ?>> parameterGetterMap, FieldMapper<S, T>[] fieldMappers) {
            this.parameterGetterMap = parameterGetterMap;
            this.fieldMappers = fieldMappers;
        }
    }
    
    private abstract class InjectionParam {
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

            FieldMapper<S, T> fieldMapper;
            FieldMapper<S, T> fieldMapperAfterConstruct;
            if (NullSetter.isNull(propertyMeta.getSetter())) {
                fieldMapper = null;
                fieldMapperAfterConstruct = null;
            } else {
                fieldMapper = wrapFieldMapperWithErrorHandler(propertyMapping.getColumnKey(), new GenericBuilderFieldMapper<S, T>(index, getter));
                fieldMapperAfterConstruct = wrapFieldMapperWithErrorHandler(propertyMapping.getColumnKey(), fieldMapperFactory.newFieldMapper(propertyMapping, mappingContextFactoryBuilder, mapperConfig.mapperBuilderErrorHandler()));
            }

            return new GenericBuilderGetterAndFieldMapper((ContextualGetter<S, Object>) getter, fieldMapper, null, fieldMapperAfterConstruct);

        }

        @Override
        public boolean needTransformer() {
            return needTransformer(propertyMeta);
        }


        private class GenericBuilderFieldMapper<S, T> implements FieldMapper<S, T> {
            private final int index;
            private final ContextualGetter<? super S, ?> getter;

            public GenericBuilderFieldMapper(int index, ContextualGetter<? super S, ?> getter) {
                this.index = index;
                this.getter = getter;
            }

            @Override
            public void mapTo(S source, T target, MappingContext<? super S> context) throws Exception {
                ((GenericBuilder)target).objects[index] = getter.get(source, context);
            }
        }
    }

    public static String getterNotFoundErrorMessage(PropertyMapping propertyMapping) {
        ClassMeta propertyClassMeta = propertyMapping.getPropertyMeta().getPropertyClassMeta();
        String currentPath = propertyMapping.getPropertyMeta().getPath();
        if (propertyClassMeta instanceof ArrayClassMeta) {

            ArrayClassMeta arrayClassMeta = (ArrayClassMeta) propertyClassMeta;

            ClassMeta elementClassMeta = arrayClassMeta.getElementClassMeta();
            if (elementClassMeta.getNumberOfProperties() <= 1) {
                String actualProp = "val";
                if (elementClassMeta.getNumberOfProperties() == 1 && elementClassMeta instanceof ObjectClassMeta) {
                    ObjectClassMeta objectClassMeta = (ObjectClassMeta) elementClassMeta;
                    actualProp = objectClassMeta.getFirstProperty().getPath();
                }
                String expectedName = currentPath + "_" + actualProp;
                return "Could not find getter for " + propertyMapping.getColumnKey() + " type "
                        + propertyMapping.getPropertyMeta().getPropertyType()
                        + ". If you meant to map to the element of the List you will need to rename the column to '" + expectedName + "' or call addAlias(\"" + currentPath + "\", \"" + expectedName + "\") on the Factory."
                        + " See " + CSFM_GETTER_NOT_FOUND.toUrl();
            }
            
        }
        return "Could not find getter for " + propertyMapping.getColumnKey() + " type "
                + propertyMapping.getPropertyMeta().getPropertyType()
                + " path " + currentPath
                + ". See " + CSFM_GETTER_NOT_FOUND.toUrl();
    }

    private class SubPropertyParam extends InjectionParam {
        final List<PropertyMapping<T, ?, K>> propertyMappings;

        private SubPropertyParam(Parameter parameter, ConstructorPropertyMeta<T, ?> propertyMeta, List<PropertyMapping<T, ?, K>> propertyMappings) {
            super(parameter, propertyMeta);
            this.propertyMappings = propertyMappings;
        }

        public GetterAndFieldMapper getterAndfieldMapper() {

            final MappingContextFactoryBuilder currentBuilder = getMapperContextFactoryBuilder(propertyMeta, propertyMappings);

            final SourceMapper<S, ?> mapper = getsSourceMapper(currentBuilder);
            ContextualGetter<S, ?> biFunction = newMapperGetterAdapter(mapper, currentBuilder);
            FieldMapper<S, T> fieldMapper = newMapperFieldMapper(propertyMappings, propertyMeta, mapper, currentBuilder);
            return new GetterAndFieldMapper(biFunction, fieldMapper);
        }

        @Override
        GenericBuilderGetterAndFieldMapper getterAndfieldMapperGenericBuilder(final int i) {
            final MappingContextFactoryBuilder currentBuilder = getMapperContextFactoryBuilder(propertyMeta, propertyMappings);

            SourceMapper<S, ?> mapper = getsSourceMapper(currentBuilder);
            Function<?, T> transform = null;

            if (mapper instanceof TransformSourceFieldMapper) {
                transform = ((TransformSourceFieldMapper) mapper).transform;
                mapper = ((TransformSourceFieldMapper) mapper).delegate;
            }
            
            ContextualGetter<S, Object> biFunction = newMapperGetterAdapter(mapper, currentBuilder);
            
            
            FieldMapper<S, T> fieldMapper = newMapperFieldMapper(propertyMappings, new GenericBuilderSetter(i), mapper , currentBuilder);
            FieldMapper<S, T> fieldMapperAfterConstruct = newMapperFieldMapper(propertyMappings, propertyMeta, mapper, currentBuilder);

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
                mapper = getterPropertyMapper(propertyMeta, propertyMappings.get(0));
            } else {
                mapper = subPropertyMapper(propertyMeta, propertyMappings, currentBuilder);
            }
            return mapper;
        }

        private class GenericBuilderSetter implements Setter {
            private final int index;

            public GenericBuilderSetter(int index) {
                this.index = index;
            }

            @Override
            public void set(Object target, Object value) throws Exception {
                ((GenericBuilder)target).objects[index] = value;
            }
        }
    }
    
    private class GetterAndFieldMapper {
        final ContextualGetter<S, ?> getter;
        final FieldMapper<S, T> fieldMapper;

        private GetterAndFieldMapper(ContextualGetter<S, ?> getter, FieldMapper<S, T> fieldMapper) {
            this.getter = getter;
            this.fieldMapper = fieldMapper;
        }
    }

    private class GenericBuilderGetterAndFieldMapper {
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
        public <P> ContextualGetter<S, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, K> mappingContextFactoryBuilder, Object... properties) {
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

}