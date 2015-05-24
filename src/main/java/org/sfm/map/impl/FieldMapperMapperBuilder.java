package org.sfm.map.impl;

import org.sfm.jdbc.impl.getter.MapperGetterAdapter;
import org.sfm.map.*;
import org.sfm.map.impl.fieldmapper.FieldMapperFactory;
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

    public static final int NO_ASM_MAPPER_THRESHOLD = 792; // see https://github.com/arnaudroger/SimpleFlatMapper/issues/152
    private static final FieldKey[] FIELD_KEYS = new FieldKey[0];

    private final Class<S> source;
	private final Type target;

	private final FieldMapperFactory<S, K> fieldMapperFactory;
	private final GetterFactory<S, K> getterFactory;

	protected final PropertyMappingsBuilder<T, K,FieldMapperColumnDefinition<K, S>> propertyMappingsBuilder;
	protected final ReflectionService reflectionService;
	
	protected final ColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> columnDefinitions;
	private final List<FieldMapper<S, T>> additionalMappers = new ArrayList<FieldMapper<S, T>>();
	protected final PropertyNameMatcherFactory propertyNameMatcherFactory;

	protected final MapperBuilderErrorHandler mapperBuilderErrorHandler;
    private FieldMapperErrorHandler<K> fieldMapperErrorHandler;
    protected final MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder;

    protected final boolean failOnAsm;
    protected final int asmMapperNbFieldsLimit;

    public FieldMapperMapperBuilder(
            final Class<S> source,
            final ClassMeta<T> classMeta,
            GetterFactory<S, K> getterFactory,
            FieldMapperFactory<S, K> fieldMapperFactory,
            ColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> columnDefinitions,
            PropertyNameMatcherFactory propertyNameMatcherFactory,
            MapperBuilderErrorHandler mapperBuilderErrorHandler,
            MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder,
            boolean failOnAsm,
            int asmMapperNbFieldsLimit) throws MapperBuildingException {
        this.mappingContextFactoryBuilder = mappingContextFactoryBuilder;
        this.failOnAsm = failOnAsm;
        this.asmMapperNbFieldsLimit = asmMapperNbFieldsLimit;
		this.source = requireNonNull("source", source);
		this.getterFactory = requireNonNull("getterFactory", getterFactory);
		this.fieldMapperFactory = requireNonNull("fieldMapperFactory", fieldMapperFactory);
		this.propertyMappingsBuilder = new PropertyMappingsBuilder<T, K, FieldMapperColumnDefinition<K, S>>(classMeta, propertyNameMatcherFactory, mapperBuilderErrorHandler);
		this.propertyNameMatcherFactory = requireNonNull("propertyNameMatcherFactory", propertyNameMatcherFactory);
		this.target = requireNonNull("classMeta", classMeta).getType();
		this.reflectionService = requireNonNull("classMeta", classMeta).getReflectionService();
		this.columnDefinitions = requireNonNull("columnDefinitions", columnDefinitions);
		this.mapperBuilderErrorHandler = requireNonNull("mapperBuilderErrorHandler", mapperBuilderErrorHandler);
	}

    @SuppressWarnings("unchecked")
    public final  FieldMapperMapperBuilder<S, T, K> addMapping(K key, final FieldMapperColumnDefinition<K, S> columnDefinition) {
        final FieldMapperColumnDefinition<K, S> composedDefinition = FieldMapperColumnDefinition.compose(columnDefinition, columnDefinitions.getColumnDefinition(key));
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


        MappingContextFactory<S> mappingContextFactory = null;

        if (mappingContextFactoryBuilder.isRoot()) {
            mappingContextFactory = mappingContextFactoryBuilder.newFactory();
        }

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
                                        source,
                                        getTargetClass(),
                                        mappingContextFactory
                                );
            } catch (Exception e) {
                if (failOnAsm) {
                    return ErrorHelper.rethrow(e);
                } else {
                    mapper = new MapperImpl<S, T>(fields, constructorFieldMappersAndInstantiator.first(), constructorFieldMappersAndInstantiator.second(), mappingContextFactory);
                }
            }
        } else {
            mapper = new MapperImpl<S, T>(fields, constructorFieldMappersAndInstantiator.first(), constructorFieldMappersAndInstantiator.second(), mappingContextFactory);
        }
        return mapper;
    }

    public void setFieldMapperErrorHandler(
            FieldMapperErrorHandler<K> errorHandler) {
        this.fieldMapperErrorHandler = errorHandler;
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
            Tuple2<Map<Parameter, Getter<S, ?>>, FieldMapper<S, T>[]> constructorInjections = constructorInjections();
            Map<Parameter, Getter<S, ?>> injections = constructorInjections.first();
            Instantiator<S, T> instantiator = instantiatorFactory.getInstantiator(source, target, propertyMappingsBuilder, injections, getterFactory);
            return new Tuple2<FieldMapper<S, T>[], Instantiator<S, T>>(constructorInjections.second(), instantiator);
		} catch(Exception e) {
            return ErrorHelper.rethrow(e);
		}
	}

	@SuppressWarnings("unchecked")
    private Tuple2<Map<Parameter, Getter<S, ?>>, FieldMapper<S, T>[]> constructorInjections() {
		final Map<Parameter, Getter<S, ?>> injections = new HashMap<Parameter, Getter<S, ?>>();
		final List<FieldMapper<S, T>> fieldMappers = new ArrayList<FieldMapper<S, T>>();
		propertyMappingsBuilder.forEachConstructorProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K, S>>>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> t) {
				PropertyMeta<T, ?> pm  = t.getPropertyMeta();
                ConstructorPropertyMeta<T, ?> cProp = (ConstructorPropertyMeta<T, ?>) pm;
                Parameter parameter = cProp.getParameter();
                injections.put(parameter, getterFor(t, parameter.getGenericType()));
			}
		});

        for(Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>> e :
                getSubPropertyPerOwner()) {
            if (e.first().isConstructorProperty()) {
                final List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> properties = e.second();

                final MappingContextFactoryBuilder currentBuilder = getMapperContextFactoryBuilder(e.first(), properties);

                final Mapper<S, ?> mapper = subPropertyMapper(e.first(), properties, currentBuilder);

                ConstructorPropertyMeta<T, ?> meta = (ConstructorPropertyMeta<T, ?>) e.first();

                injections.put(meta.getParameter(), newMapperGetterAdapter(mapper, currentBuilder));
                fieldMappers.add(newMapperFieldMapper(properties, meta, mapper, currentBuilder));
            }
        }
		return new Tuple2<Map<Parameter, Getter<S, ?>>, FieldMapper<S, T>[]>(injections, fieldMappers.toArray(new FieldMapper[0]));
	}

    private MappingContextFactoryBuilder getMapperContextFactoryBuilder(PropertyMeta<?, ?> owner, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> properties) {
        final List<K> subKeys = getSubKeys(properties);
        return mappingContextFactoryBuilder.newBuilder(subKeys, owner);
    }

    @SuppressWarnings("unchecked")
    private <P> FieldMapper<S, T> newMapperFieldMapper(List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> properties, PropertyMeta<T, ?> meta, Mapper<S, ?> mapper, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {

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

        return wrapFieldMapperWithErrorHandler(properties.get(0), fieldMapper );
    }

    @SuppressWarnings("unchecked")
    private <P> Getter<S,P> newMapperGetterAdapter(Mapper<S, ?> mapper, MappingContextFactoryBuilder<S, K> builder) {
        return new MapperGetterAdapter<S, P>((Mapper<S, P>)mapper, builder.nullChecker());
    }

    // call use towards sub mapper
    // the keys are initialised
    private <P> void addMapping(K columnKey, FieldMapperColumnDefinition<K, S> columnDefinition,  PropertyMeta<T, P> prop) {
		propertyMappingsBuilder.addProperty(columnKey, columnDefinition, prop);
	}

	@SuppressWarnings("unchecked")
	private final FieldMapper<S, T>[] fields() {
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

        for(Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>> e :
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


    private List<Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>>> getSubPropertyPerOwner() {

        final List<Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>>> subPropertiesList = new ArrayList<Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>>>();

        propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,K, FieldMapperColumnDefinition<K, S>>>() {

            @SuppressWarnings("unchecked")
            @Override
            public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> t) {
                if (t == null) return;
                PropertyMeta<T, ?> meta = t.getPropertyMeta();
                if (meta == null) return;
                if (meta.isSubProperty()) {
                    addSubProperty(t, (SubPropertyMeta<T, ?, ?>) meta, t.getColumnKey());
                }
            }
            private <P> void addSubProperty(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> pm,  SubPropertyMeta<T, ?, ?> subPropertyMeta, K key) {
                PropertyMeta<T, ?> propertyOwner = subPropertyMeta.getOwnerProperty();
                List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> props = getList(propertyOwner);
                if (props == null) {
                    props = new ArrayList<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>();
                    subPropertiesList.add(new Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>>(propertyOwner, props));
                }
                props.add(pm);
            }

            private List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> getList(PropertyMeta<?, ?> owner) {
                for(Tuple2<PropertyMeta<T, ?>, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>>> tuple : subPropertiesList) {
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
    private <P> Mapper<S, P> subPropertyMapper(PropertyMeta<T, P> owner, List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> properties, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {
        final FieldMapperMapperBuilder<S, P, K> builder = newSubBuilder(owner.getPropertyClassMeta(), mappingContextFactoryBuilder);

        for(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> pm : properties) {
            final SubPropertyMeta<T, P,  ?> propertyMeta = (SubPropertyMeta<T, P,  ?>) pm.getPropertyMeta();
            final PropertyMeta<P, ?> subProperty = ((SubPropertyMeta<T, P, ?>) propertyMeta).getSubProperty();
            builder.addMapping(pm.getColumnKey(), pm.getColumnDefinition(), subProperty);
        }
        return builder.mapper();
    }

	@SuppressWarnings("unchecked")
	protected <P> FieldMapper<S, T> newFieldMapper(PropertyMapping<T, P, K, FieldMapperColumnDefinition<K, S>> t) {
		FieldMapper<S, T> fieldMapper = (FieldMapper<S, T>) t.getColumnDefinition().getCustomFieldMapper();

		if (fieldMapper == null) {
			fieldMapper = fieldMapperFactory.newFieldMapper(t, mapperBuilderErrorHandler);
		}

        return wrapFieldMapperWithErrorHandler(t, fieldMapper);
	}

    private <P> FieldMapper<S, T> wrapFieldMapperWithErrorHandler(final PropertyMapping<T, P, K, FieldMapperColumnDefinition<K, S>> t, final FieldMapper<S, T> fieldMapper) {
        if (fieldMapperErrorHandler != null
            && !(fieldMapperErrorHandler instanceof RethrowFieldMapperErrorHandler)
            && fieldMapper != null) {
            return new FieldErrorHandlerMapper<S, T, K>(t.getColumnKey(), fieldMapper, fieldMapperErrorHandler);
        }
        return fieldMapper;
    }

    private Getter<S, ?> getterFor(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> t, Type paramType) {
		Getter<S, ?> getter = t.getColumnDefinition().getCustomGetter();

		if (getter == null) {
			getter = getterFactory.newGetter(paramType, t.getColumnKey(), t.getColumnDefinition());
		}
		if (getter == null) {
			mapperBuilderErrorHandler.getterNotFound("Could not find getter for " + t.getColumnKey() + " type " + paramType);
		}
		return getter;
	}


    public void addMapper(FieldMapper<S, T> mapper) {
		additionalMappers.add(mapper);
	}

    private final <ST> FieldMapperMapperBuilder<S, ST, K> newSubBuilder(ClassMeta<ST> classMeta, MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder) {
        return new FieldMapperMapperBuilder<S, ST, K>(
                source,
                classMeta,
                getterFactory,
                fieldMapperFactory,
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                mappingContextFactoryBuilder,
                failOnAsm,
                asmMapperNbFieldsLimit);
    }




    private FieldKey<?>[] getKeys() {
        return propertyMappingsBuilder.getKeys().toArray(FIELD_KEYS);
    }

    private boolean isEligibleForAsmMapper() {
        return reflectionService.isAsmActivated()
                && propertyMappingsBuilder.size() < asmMapperNbFieldsLimit;
    }

    @SuppressWarnings("unchecked")
    private List<K> getSubKeys(List<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>>> properties) {
        List<K> keys = new ArrayList<K>();

        // look for keys property of the object
        for (PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K, S>> pm : properties) {
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