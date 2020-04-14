package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.*;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.error.RethrowConsumerErrorHandler;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.map.getter.ComposedContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.property.*;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.IndexedGetter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.property.SpeculativeArrayIndexResolutionProperty;
import org.simpleflatmapper.reflect.property.SpeculativeObjectLookUpProperty;
import org.simpleflatmapper.util.*;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.*;


// I don't really like using inheritance but did not see any other way
// to avoid rewriting a lot of delegate method...
@SuppressWarnings("unchecked")
public abstract class AbstractMapperFactory<
		K extends FieldKey<K>,
		MF extends AbstractMapperFactory<K, MF, S>, S> {


	private FieldMapperErrorHandler<K> fieldMapperErrorHandler = null;
    private MapperBuilderErrorHandler mapperBuilderErrorHandler = RethrowMapperBuilderErrorHandler.INSTANCE;
    private ConsumerErrorHandler consumerErrorHandler = RethrowConsumerErrorHandler.INSTANCE;

    private final AbstractColumnDefinitionProvider<K> columnDefinitions;
    private final List<TypedPredicatedPredicatedColumnPropertyFactory<K>> typedPredicatedPredicatedColunnPropertyFactories = new ArrayList<TypedPredicatedPredicatedColumnPropertyFactory<K>>();
    protected final List<MapperConfig.Discriminator<S, K, ?>> discriminators = new ArrayList<MapperConfig.Discriminator<S, K, ?>>();
	private final ColumnDefinition<K, ?> identity;

	private boolean useAsm = true;
    private boolean failOnAsm = false;
    private int asmMapperNbFieldsLimit = MapperConfig.NO_ASM_MAPPER_THRESHOLD;

	private PropertyNameMatcherFactory propertyNameMatcherFactory = DefaultPropertyNameMatcherFactory.DEFAULT;

    private ReflectionService reflectionService = null;
	private int maxMethodSize = MapperConfig.MAX_METHOD_SIZE;
	private boolean assumeInjectionModifiesValues;
	
	private Predicate<? super S> rowFilter = null;
	private boolean unorderedJoin;

	protected ContextualGetterFactory<? super S, K> getterFactory;

	public AbstractMapperFactory(AbstractMapperFactory<K, ?, S> config) {
		this.fieldMapperErrorHandler = config.fieldMapperErrorHandler;
		this.mapperBuilderErrorHandler = config.mapperBuilderErrorHandler;
		this.consumerErrorHandler = config.consumerErrorHandler;

		this.columnDefinitions = config.columnDefinitions;
		this.identity = config.identity;
		
		this.useAsm = config.useAsm;
		this.failOnAsm = config.failOnAsm;
		this.asmMapperNbFieldsLimit = config.asmMapperNbFieldsLimit;
		
		this.propertyNameMatcherFactory = config.propertyNameMatcherFactory;
		
		this.reflectionService = config.reflectionService;
		this.maxMethodSize = config.maxMethodSize;
		this.assumeInjectionModifiesValues = config.assumeInjectionModifiesValues;
		this.rowFilter = config.rowFilter;
		this.unorderedJoin = config.unorderedJoin;
		this.getterFactory = config.getterFactory;
	}


	public AbstractMapperFactory(AbstractColumnDefinitionProvider<K> columnDefinitions, ColumnDefinition<K, ?> identity, ContextualGetterFactory<? super S, K> getterFactory) {
		this.columnDefinitions = columnDefinitions;
		this.identity = identity;
		this.getterFactory = getterFactory;
	}

	public AbstractMapperFactory(AbstractColumnDefinitionProvider<K> columnDefinitions, ColumnDefinition<K, ?> identity, Function<MF, ? extends ContextualGetterFactory<? super S, K>> getterFactoryFactory) {
		this.columnDefinitions = columnDefinitions;
		this.identity = identity;
		this.getterFactory = getterFactoryFactory.apply((MF)this);
	}

	/**
     * the FieldMapperErrorHandler is called when a error occurred when mapping a field from the source to the target.
     * By default it just throw the Exception.
     * @param fieldMapperErrorHandler the new FieldMapperErrorHandler
     * @return the current factory
     */
	public final MF fieldMapperErrorHandler(final FieldMapperErrorHandler<K> fieldMapperErrorHandler) {
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		return (MF) this;
	}

	/**
	 * Enabled support for unordered join at the root level.
	 * To support that the mapper will need to load the data on the first call and keep a map of id to object.
	 * It is more costly to enable that use with caution.
	 * 
	 * @return the current factory
	 */
	public final MF unorderedJoin() {
		this.unorderedJoin = true;
		return (MF) this;
	}

    /**
     * Change the mapperBuilderErrorHandler to an IgnoreMapperBuilderErrorHandler.
     * @return the current factory
     */
    public final MF ignorePropertyNotFound() {
    	return addColumnProperty(ConstantPredicate.truePredicate(), OptionalProperty.INSTANCE);
    }

	/**
	 * enabled speculative property look up on object.
	 * @return this
	 */
	public final MF enableSpeculativePropertyLookupOnObject() {
    	return addColumnProperty(ConstantPredicate.truePredicate(), SpeculativeObjectLookUpProperty.INSTANCE);
	}

    /**
	 * Set the new MapperBuilderErrorHandler. the MapperBuilderErrorHandler is called when an error occurred or a property is not found in the builder while creating the jdbcMapper.
	 * @param mapperBuilderErrorHandler the MapperBuilderErrorHandler
	 * @return the current factory
	 */
	public final MF mapperBuilderErrorHandler(final MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		return (MF) this;
	}


    /**
     * the ConsumerErrorHandler is called when an exception is thrown by the CheckedConsumer in the forEach call.
     * @param consumerErrorHandler the new ConsumerErrorHandler
     * @return the current factory
     */
	public final MF consumerErrorHandler(final ConsumerErrorHandler consumerErrorHandler) {
		this.consumerErrorHandler = consumerErrorHandler;
		return (MF) this;
	}

	@Deprecated
	public final MF rowHandlerErrorHandler(final ConsumerErrorHandler rowHandlerErrorHandler) {
		return consumerErrorHandler(rowHandlerErrorHandler);
	}

	/**
	 *
	 * @param useAsm false if you want to disable asm generation of Mappers, Getter and Setter. This would be active by default if asm is present in a compatible version.
	 * @return the current factory
	 */
	public final MF useAsm(final boolean useAsm) {
		if (reflectionService != null)  throw new IllegalStateException("Reflection service is set cannot change useAsm");
		this.useAsm = useAsm;
		return (MF) this;
	}

    /**
     * Override the default implementation of the ReflectionService.
     * @param reflectionService the overriding newInstance
     * @return the current factory
     */
    public final MF reflectionService(final ReflectionService reflectionService) {
        this.reflectionService = reflectionService;
        return (MF) this;
    }

	public final MF rowFilter(final Predicate<? super S> rowFilter) {
		this.rowFilter = rowFilter;
		return (MF) this;
	}
	public final MapperConfig<K, S> mapperConfig() {
		return mapperConfig(Object.class);
	}
	public final MapperConfig<K, S> mapperConfig(Type targetType) {
		return MapperConfig
				.<K, S>config(enrichColumnDefinitions(columnDefinitions(targetType)))
				.mapperBuilderErrorHandler(mapperBuilderErrorHandler)
				.propertyNameMatcherFactory(propertyNameMatcherFactory)
				.failOnAsm(failOnAsm)
				.asmMapperNbFieldsLimit(asmMapperNbFieldsLimit)
				.fieldMapperErrorHandler(fieldMapperErrorHandler)
				.consumerErrorHandler(consumerErrorHandler)
				.maxMethodSize(maxMethodSize)
				.assumeInjectionModifiesValues(assumeInjectionModifiesValues)
				.discriminators(discriminators)
				.rowFilter(rowFilter)
				.unorderedJoin(unorderedJoin);
	}

	public AbstractColumnDefinitionProvider<K> enrichColumnDefinitions(AbstractColumnDefinitionProvider<K> columnDefinitions) {
		return columnDefinitions;
	}

	/**
	 * Associate an alias on the property key to rename to value.
	 * @param column the column name to rename
	 * @param actualPropertyName then name to rename to match the actual property name
	 * @return the current factory
	 */
	public final MF addAlias(String column, String actualPropertyName) {
		return addColumnDefinition(column,  identity.addRename(actualPropertyName));
	}

    /**
     * Associate an alias on the property key to rename to value on the specific type.
     * @param column the column name to rename
     * @param actualPropertyName then name to rename to match the actual property name
     * @return the current factory
     */
    public final MF addAliasForType(Type type, String column, String actualPropertyName) {
        return addColumnPropertyForType(type, column,  new RenameProperty(actualPropertyName));
    }

    /**
     * Associate the specified columnDefinition to the specified property.
     * @param column the name of the column
     * @param columnDefinition the columnDefinition
     * @return the current factory
     */
	public final MF addColumnDefinition(String column, ColumnDefinition<K, ?> columnDefinition) {
		columnDefinitions.addColumnDefinition(column, columnDefinition);
		return (MF) this;
	}

    /**
     * Associate the specified columnDefinition to the property matching the predicate.
     * @param predicate the property predicate
     * @param columnDefinition the columnDefinition
     * @return the current factory
     */
	public final MF addColumnDefinition(Predicate<? super K> predicate, ColumnDefinition<K, ?> columnDefinition) {
		columnDefinitions.addColumnDefinition(predicate, columnDefinition);
		return (MF) this;
	}

	/**
	 * Associate the specified columnProperties to the property matching the predicate.
	 * @param column the column name
	 * @param properties the properties
	 * @return the current factory
	 */
	public final MF addColumnProperty(String column, Object... properties) {
		for(Object property : properties) {
			columnDefinitions.addColumnProperty(column, property);
		}
		return (MF) this;
	}

	/**
	 * Associate the specified columnProperties to the property matching the predicate.
	 * @param predicate the property predicate
	 * @param properties the properties
	 * @return the current factory
	 */
	public final MF addColumnProperty(Predicate<? super K> predicate, Object... properties) {
		for(Object property : properties) {
			columnDefinitions.addColumnProperty(predicate, property);
		}
		return (MF) this;
	}

	/**
	 * Associate the specified columnProperties to the property matching the predicate.
	 * @param predicate the property predicate
	 * @param propertyFactory the properties
	 * @return the current factory
	 */
	public final MF addColumnProperty(Predicate<? super K> predicate, UnaryFactory<K, Object> propertyFactory) {
		columnDefinitions.addColumnProperty(predicate, propertyFactory);
		return (MF) this;
	}

	/**
	 * Associate the specified columnProperties to the property matching the key predicate and type.
	 * @param type the type
	 * @param column the property predicate
	 * @param properties the properties
	 * @return the current factory
	 */
	public final MF addColumnPropertyForType(Type type, String column, Object... properties) {
		return addColumnPropertyForType(type, CaseInsensitiveFieldKeyNamePredicate.of(column), properties);
	}

	/**
	 * Associate the specified columnProperties to the property matching the key predicate and type.
	 * @param type the type
	 * @param keyPredicate the property predicate
	 * @param properties the properties
	 * @return the current factory
	 */
	public final MF addColumnPropertyForType(Type type, Predicate<? super K> keyPredicate, Object... properties) {
		for(final Object property : properties) {
			addColumnPropertyForType(type, keyPredicate, new UnaryFactory<K, Object>() {
				@Override
				public Object newInstance(K k) {
					return property;
				}
			});
		}
		return (MF) this;
	}


	/**
	 * Associate the specified columnProperties to the property matching the key predicate and type.
	 * @param type the type
	 * @param keyPredicate the property predicate
	 * @param propertyFactory the properties
	 * @return the current factory
	 */
	public final MF addColumnPropertyForType(final Type type, Predicate<? super K> keyPredicate, UnaryFactory<K, Object> propertyFactory) {
		return addColumnPropertyForType(new Predicate<Type>() {
			@Override
			public boolean test(Type t) {
				return TypeHelper.areEquals(type, t);
			}
		}, keyPredicate, propertyFactory);
	}

	/**
	 * Associate the specified columnProperties to the property matching the key predicate and type predicate.
	 * @param typePredicate the type predicate
	 * @param keyPredicate the property predicate
	 * @param propertyFactory the properties
	 * @return the current factory
	 */
	public final MF addColumnPropertyForType(Predicate<Type> typePredicate, Predicate<? super K> keyPredicate, UnaryFactory<K, Object> propertyFactory) {
		typedPredicatedPredicatedColunnPropertyFactories.add(new TypedPredicatedPredicatedColumnPropertyFactory<K>(typePredicate, new AbstractColumnDefinitionProvider.PredicatedColumnPropertyFactory(keyPredicate, propertyFactory)));
		return (MF) this;
	}

	/**
     * Override the default PropertyNameMatcherFactory with the specified factory.
     * @param propertyNameMatcherFactory the factory
     * @return the current factory
     */
	public final MF propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		return (MF) this;
	}

	/**
	 * Override the default PropertyNameMatcherFactory with the specified factory.
	 * @param propertyNameMatcherFactorySetup a function that takes the current factory and return the newly configured one
	 * @return the current factory
	 */
	//IFJAVA8_START
	public final MF propertyNameMatcherFactory(Function<PropertyNameMatcherFactory, PropertyNameMatcherFactory> propertyNameMatcherFactorySetup) {
		this.propertyNameMatcherFactory = propertyNameMatcherFactorySetup.apply(propertyNameMatcherFactory);
		return (MF) this;
	}
	//IFJAVA8_END

    /**
     * Associate the aliases value to the property key.
     * @param aliases the key value pair
     * @return the current factory
     */
    public final MF addAliases(Map<String, String> aliases) {
		for(Map.Entry<String, String> e : aliases.entrySet()) {
			addAlias(e.getKey(), e.getValue());
		}
		return (MF) this;
	}

	/**
	 * Ignore column that match the predicate.
	 * @param predicate the predicate.
	 * @return the current factory
	 */
	public final MF ignoreColumns(Predicate<? super K> predicate) {
    	return addColumnProperty(predicate, new IgnoreProperty());
	}

	/**
	 * Ignore column with the specified names, case insensitive.
	 * @param columnNames the columnNames.
	 * @return the current factory
	 */
	public final MF ignoreColumns(String... columnNames) {
		return ignoreColumns(Arrays.asList(columnNames));
	}


	public MF addGetterFactory(ContextualGetterFactory<S, K> getterFactory) {
		this.getterFactory = ComposedContextualGetterFactory.composed(getterFactory, this.getterFactory);
		return (MF) this;
	}


	public MF addGetterForType(final Type type, final Function<K, ContextualGetter<S, ?>> getterFactory) {
		return addGetterForType(new Predicate<Type>() {
			@Override
			public boolean test(Type t) {
				return TypeHelper.isAssignable(t, type);
			}
		}, getterFactory);
	}

	public MF addGetterForType(final Type type, final ContextualGetterFactory<S, K> getterFactory) {
		return addGetterForType(new Predicate<Type>() {
			@Override
			public boolean test(Type t) {
				return TypeHelper.isAssignable(t, type);
			}
		}, getterFactory);
	}

	public <T> MF addGetterForType(final Type type, final IndexedGetter<S, T> indexedGetter) {
		return addColumnProperty(ConstantPredicate.truePredicate(), GetterFactoryProperty.<S, K, T>forType(type, indexedGetter));
	}

	public MF addGetterForType(final Predicate<Type> typePredicate, final Function<K, ContextualGetter<S, ?>> getterFactory) {
		return addGetterFactory(new ContextualGetterFactory<S, K>() {
			@Override
			public <P> ContextualGetter<S, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
				if (typePredicate.test(target)) {
					return (ContextualGetter<S, P>) getterFactory.apply(key);
				}
				return null;
			}
		});
	}

	public MF addGetterForType(final Predicate<Type> typePredicate, final ContextualGetterFactory<S, K> getterFactory) {
		return addGetterFactory(new ContextualGetterFactory<S, K>() {
			@Override
			public <P> ContextualGetter<S, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
				if (typePredicate.test(target)) {
					return getterFactory.newGetter(target, key, mappingContextFactoryBuilder, properties);
				}
				return null;
			}
		});
	}


	/**
	 * Ignore column with the specified names, case insensitive.
	 * @param columnNames the columnNames.
	 * @return the current factory
	 */
	public final MF ignoreColumns(final Collection<String> columnNames) {
		final Set<String> columnSet = new HashSet<String>();
		for(String c : columnNames) columnSet.add(c.toUpperCase());
		return ignoreColumns(new Predicate<K>() {
			@Override
			public boolean test(K k) {
				return columnSet.contains(k.getName().toUpperCase());
			}
		});
	}

    /**
     * @param b true if we want the builder to fail on asm generation failure
     * @return the current factory
     */
    public final MF failOnAsm(boolean b) {
        this.failOnAsm = b;
        return (MF) this;
    }

	/**
	 * if set to true, it will assume any constructor that takes a list as a constructor argument will need
	 * to be mapped using a builder to protect against the actual value being changed in the constructor.
	 * 
	 * @param b true to make the factory being paranoid about constructor injection of aggregation
	 * @return the current factory
	 */
	public final MF assumeInjectionModifiesValues(boolean b) {
    	this.assumeInjectionModifiesValues = b;
    	return (MF) this;
	}
	
    /**
     * change the number of fields threshold after which an asm jdbcMapper is not generated.
     * <p>
     * the default value is calculated from the benchmark results, currently 240.
     * @param asmMapperNbFieldsLimit the limit after which it does not use asm for the jdbcMapper.
     * @return the factory
     */
    public final MF asmMapperNbFieldsLimit(final int asmMapperNbFieldsLimit) {
        this.asmMapperNbFieldsLimit = asmMapperNbFieldsLimit;
        return (MF) this;
    }

	/**
	 * Number needs to be a power of 2, do not use if you don't know what it does.
	 * @param maxMethodSize the max method size, needs be a power of 2.
	 * @return the factory.
	 */
	public final MF maxMethodSize(final int maxMethodSize) {
		this.maxMethodSize = maxMethodSize;
		return (MF) this;
	}


	/**
     * Mark the specified columns as keys.
     * @param columns the columns
     * @return  the current factory
     */
    public final MF addKeys(String... columns) {
        for(String col : columns) {
            addColumnDefinition(col, identity.addKey());
        }
        return (MF) this;
    }


    /**
     * @return the current ConsumerErrorHandler
     */
    public final ConsumerErrorHandler consumerErrorHandler() {
        return consumerErrorHandler;
    }


	public final <T> ClassMeta<T> getClassMeta(TypeReference<T> target) {
		return getClassMeta(target.getType());
	}

	public final <T> ClassMeta<T> getClassMeta(Class<T> target) {
		return getClassMeta((Type)target);
	}

    public final <T> ClassMeta<T> getClassMeta(Type target) {
        return getReflectionService().getClassMeta(target);
    }

	public final <T> ClassMeta<T> getClassMetaWithExtraInstantiator(TypeReference<T> target, Member instantiator) {
		return getClassMetaWithExtraInstantiator(target.getType(), instantiator);
	}

	public final <T> ClassMeta<T> getClassMetaWithExtraInstantiator(Class<T> target, Member instantiator) {
		return getClassMetaWithExtraInstantiator((Type) target, instantiator);
	}

	public final <T> ClassMeta<T> getClassMetaWithExtraInstantiator(Type target, Member instantiator) {
		return getReflectionService().getClassMetaExtraInstantiator(target, instantiator);
	}


	public ReflectionService getReflectionService() {
        if (reflectionService == null) {
			reflectionService =  ReflectionService.newInstance(useAsm);
		}
		return reflectionService;
	}

	public ColumnDefinitionProvider<K> columnDefinitions() {
		return columnDefinitions;
	}

	public AbstractColumnDefinitionProvider<K> columnDefinitions(Type targetType) {
		AbstractColumnDefinitionProvider<K> provider = columnDefinitions.copy();

		for(TypedPredicatedPredicatedColumnPropertyFactory f : typedPredicatedPredicatedColunnPropertyFactories) {
			if (f.predicate.test(targetType)) {
				provider.addColumnProperty(f.predicatedColumnPropertyFactory);
			}
		}

		return provider;
	}


	public <T> DiscriminatorDSL<K, MF, S, T> discriminator(Class<T> commonType) {
		return discriminator((Type)commonType);
	}

	public <T> DiscriminatorDSL<K, MF, S, T> discriminator(Type commonType) {
    	return new DiscriminatorDSL<K, MF, S, T>((MF) this, commonType);
	}


	/**
	 * @deprecated use {@link #discriminator(Type)} dsl
	 */
	@Deprecated
	public <T> MF discriminator(Type commonType, Consumer<DiscriminatorBuilder<S, K, T>> consumer) {
		return this.<T>discriminator(commonType)._with(consumer);
	}

	/**
	 * @deprecated use {@link #discriminator(Type)} dsl
	 */
	@Deprecated
	public <T, KT> MF discriminator(Type commonType, final Getter<? super S, KT> getter, Consumer<DiscriminatorConditionBuilder<S, K, KT, T>> consumer) {
		return this.<T>discriminator(commonType).onGetter(getter).with(consumer);
	}

	/**
	 * @deprecated use {@link #discriminator(Type)} dsl
	 */
	@Deprecated
	public <T> MF discriminator(Class<T> commonType, Consumer<DiscriminatorBuilder<S, K, T>> consumer) {
		return discriminator((Type)commonType, consumer);
	}

	/**
	 * @deprecated use {@link #discriminator(Type)} dsl
	 */
	@Deprecated
	public <T, V> MF discriminator(Class<T> commonType, Getter<? super S, V> getter, Consumer<DiscriminatorConditionBuilder<S, K, V, T>> consumer) {
		return discriminator((Type)commonType, getter, consumer);
	}

	/**
	 * @deprecated use {@link #discriminator(Type)} dsl
	 */
	@Deprecated
	public <T, V> MF discriminator(Class<T> commonType, final String discriminatorColumn, CheckedBiFunction<S, String, V> discriminatorFieldAccessor, Consumer<DiscriminatorConditionBuilder<S, K, V, T>> consumer) {
		return discriminator((Type) commonType, discriminatorColumn, discriminatorFieldAccessor, consumer);
	}

	/**
	 * @deprecated use {@link #discriminator(Type)} dsl
	 */
	@Deprecated
	public <T, V> MF discriminator(Type commonType, final String discriminatorColumn, final CheckedBiFunction<S, String, V> discriminatorFieldAccessor, Consumer<DiscriminatorConditionBuilder<S, K, V, T>> consumer) {
    	return this.<T>discriminator(commonType).onColumnWithNamedGetter(discriminatorColumn, discriminatorFieldAccessor).with(consumer);
	}



	public MF enableSpeculativeArrayIndexResolution() {
    	addColumnProperty(ConstantPredicate.truePredicate(), SpeculativeArrayIndexResolutionProperty.INSTANCE);
		return (MF) this;
	}

    public static class DiscriminatorConditionBuilder<S, K extends FieldKey<K>, KT, T> {
    	private final DiscriminatorBuilder<S, K, T> discriminatorBuilder;
    	private final Function<List<K>, Getter<? super S, ? extends KT>> getterFactory;

		public DiscriminatorConditionBuilder(DiscriminatorBuilder<S, K, T> discriminatorBuilder, Function<List<K>, Getter<? super S, ? extends KT>> getterFactory) {
			this.discriminatorBuilder = discriminatorBuilder;
			this.getterFactory = getterFactory;
		}

		public DiscriminatorConditionBuilder<S, K, KT, T> when(KT value, Type type) {
			return discriminatorCase(value, type);
		}
		public DiscriminatorConditionBuilder<S, K, KT, T> when(KT value, Class<T> type) {
			return discriminatorCase(value, type);
		}
		public DiscriminatorConditionBuilder<S, K, KT, T> when(KT value, ClassMeta<? extends T> classMeta) {
			return discriminatorCase(value, classMeta);
		}

		public DiscriminatorConditionBuilder<S, K, KT, T> when(Predicate<KT> predicate, Type type) {
			return discriminatorCase(predicate, type);
		}
		
		public DiscriminatorConditionBuilder<S, K, KT, T> when(Predicate<KT> predicate, Class<T> type) {
			return discriminatorCase(predicate, type);
		}
		public DiscriminatorConditionBuilder<S, K, KT, T> when(Predicate<KT> predicate, ClassMeta<? extends T> classMeta) {
			return discriminatorCase(predicate, classMeta);
		}

		/**
		 * @deprecated use {@link #when(Object, Type)}
		 */
		@Deprecated
		public DiscriminatorConditionBuilder<S, K, KT, T> discriminatorCase(KT value, Type type) {
			return discriminatorCase(toEqualsPredicate(value), type);
		}

		/**
		 * @deprecated use {@link #when(Object, Class)} ()} instead.
		 */
		@Deprecated
		public DiscriminatorConditionBuilder<S, K, KT, T> discriminatorCase(KT value, Class<T> type) {
			return discriminatorCase(toEqualsPredicate(value), type);
		}

		/**
		 * @deprecated use {@link #when(Object, ClassMeta)} ()} instead.
		 */
		@Deprecated
		public DiscriminatorConditionBuilder<S, K, KT, T> discriminatorCase(KT value, ClassMeta<? extends T> classMeta) {
			return discriminatorCase(toEqualsPredicate(value), classMeta);
		}

		/**
		 * @deprecated use {@link #when(Predicate, Type)} ()} instead.
		 */
		@Deprecated
		public DiscriminatorConditionBuilder<S, K, KT, T> discriminatorCase(Predicate<KT> predicate, Type type) {
			discriminatorBuilder.when(toSourcePredicate(predicate), type);
			return this;
		}


		/**
		 * @deprecated use {@link #when(Predicate, Class)} ()} instead.
		 */
		@Deprecated
		public DiscriminatorConditionBuilder<S, K, KT, T> discriminatorCase(Predicate<KT> predicate, Class<T> type) {
			discriminatorBuilder.when(toSourcePredicate(predicate), type);
			return this;
		}
		/**
		 * @deprecated use {@link #when(Predicate, ClassMeta)} ()} instead.
		 */
		@Deprecated
		public DiscriminatorConditionBuilder<S, K, KT, T> discriminatorCase(Predicate<KT> predicate, ClassMeta<? extends T> classMeta) {
			discriminatorBuilder.when(toSourcePredicate(predicate), classMeta);
			return this;
		}

		private Predicate<KT> toEqualsPredicate(KT value) {
			return EqualsPredicate.<KT>of(value);
		}
		
		private Function<List<K>, Predicate<S>> toSourcePredicate(final Predicate<KT> predicate) {
			return new Function<List<K>, Predicate<S>>() {
				@Override
				public Predicate<S> apply(List<K> ks) {
					Getter<? super S, ? extends KT> getter = getterFactory.apply(ks);
					return new SourcePredicate<S, KT>(predicate, getter);
				}
			};
		}

		static class SourcePredicate<S, V> implements Predicate<S> {
			final Predicate<? super V> predicate;
			final Getter<? super S, ? extends V> getter;

			public SourcePredicate(Predicate<? super V> predicate, Getter<? super S, ? extends V> getter) {
				this.predicate = predicate;
				this.getter = getter;
			}

			@Override
			public boolean test(S s) {
				try {
					return predicate.test(getter.get(s));
				} catch (Exception e) {
					return ErrorHelper.rethrow(e);
				}
			}

			@Override
			public boolean equals(Object o) {
				if (this == o) return true;
				if (o == null || getClass() != o.getClass()) return false;

				SourcePredicate<?, ?> that = (SourcePredicate<?, ?>) o;

				if (predicate != null ? !predicate.equals(that.predicate) : that.predicate != null) return false;
				return getter != null ? getter.equals(that.getter) : that.getter == null;
			}

			@Override
			public int hashCode() {
				int result = predicate != null ? predicate.hashCode() : 0;
				result = 31 * result + (getter != null ? getter.hashCode() : 0);
				return result;
			}
		}
	}

	public static final class DiscriminatorBuilder<S, K extends FieldKey<K>, T> {

		private final Type commonType;
		private final ReflectionService reflectionService;

		List<MapperConfig.DiscriminatorCase<S, K, T>> cases = new ArrayList<MapperConfig.DiscriminatorCase<S, K, T>>();

		public DiscriminatorBuilder(Type type, ReflectionService reflectionService) {
			this.commonType = type;
			this.reflectionService = reflectionService;
		}

		public DiscriminatorBuilder<S, K, T> when(Function<List<K>, Predicate<S>> predicateFactory, ClassMeta<? extends T> classMeta) {
			return discriminatorCase(predicateFactory, classMeta);
		}

		public DiscriminatorBuilder<S, K, T> when(Function<List<K>, Predicate<S>> predicateFactory, Class<? extends T> target) {
			return when(predicateFactory, reflectionService.getClassMeta(target));
		}
		public DiscriminatorBuilder<S, K, T> when(Function<List<K>, Predicate<S>> predicateFactory, Type target) {
			if (!TypeHelper.isAssignable(commonType, target)) {
				throw new IllegalArgumentException("type " + target + " is not a subclass of " + commonType);
			}
			return discriminatorCase(predicateFactory, reflectionService.<T>getClassMeta(target));
		}

		public DiscriminatorBuilder<S, K, T> when(Predicate<S> predicate, ClassMeta<? extends T> classMeta) {
			return discriminatorCase(predicate, classMeta);
		}

		public DiscriminatorBuilder<S, K, T> when(Predicate<S> predicate, Class<? extends T> target) {
			return discriminatorCase(predicate, target);
		}
		public DiscriminatorBuilder<S, K, T> when(Predicate<S> predicate, Type target) {
			return discriminatorCase(predicate, target);
		}

		public DiscriminatorBuilder<S, K, T> defaultType(Class<? extends T> target) {
			return defaultType((Type) target);
		}

		public DiscriminatorBuilder<S, K, T> defaultType(Type target) {
			return discriminatorCase(ConstantPredicate.<S>truePredicate(), target);
		}
		
		public DiscriminatorBuilder<S, K, T> discriminatorCase(final Predicate<S> predicate, ClassMeta<? extends T> classMeta) {
			return discriminatorCase(new Function<List<K>, Predicate<S>>() {

										 @Override
										 public Predicate<S> apply(List<K> ks) {
											 return predicate;
										 }
									 }, classMeta
			);
		}

		public  DiscriminatorBuilder<S, K, T> discriminatorCase(final Function<List<K>, Predicate<S>> predicateFactory, ClassMeta<? extends T> classMeta) {
			MapperConfig.DiscriminatorCase<S, K, T> dCase = new MapperConfig.DiscriminatorCase<S, K, T>(predicateFactory, classMeta);
			cases.add(dCase);
			return this;
		}
		
		public DiscriminatorBuilder<S, K, T> discriminatorCase(Predicate<S> predicate, Class<? extends T> target) {
			return discriminatorCase(predicate, reflectionService.getClassMeta(target));
		}
		public DiscriminatorBuilder<S, K, T> discriminatorCase(Predicate<S> predicate, Type target) {
			if (!TypeHelper.isAssignable(commonType, target)) {
				throw new IllegalArgumentException("type " + target + " is not a subclass of " + commonType);
			}
			return discriminatorCase(predicate, reflectionService.<T>getClassMeta(target));
		}


	}

	private class TypedPredicatedPredicatedColumnPropertyFactory<K> {
    	private final Predicate<Type> predicate;
    	private final AbstractColumnDefinitionProvider.PredicatedColumnPropertyFactory predicatedColumnPropertyFactory;

		private TypedPredicatedPredicatedColumnPropertyFactory(Predicate<Type> predicate, AbstractColumnDefinitionProvider.PredicatedColumnPropertyFactory predicatedColumnPropertyFactory) {
			this.predicate = predicate;
			this.predicatedColumnPropertyFactory = predicatedColumnPropertyFactory;
		}
	}


	public static class DiscriminatorDSL<K extends FieldKey<K>, MF extends AbstractMapperFactory<K, MF, S>, S, T> {
    	private final MF mapperFactory;
    	private final Type commonType;
    	private final Object discriminatorId = new Object();

		public DiscriminatorDSL(MF mapperFactory, Type commonType) {
			this.mapperFactory = mapperFactory;
			this.commonType = commonType;
		}

		public <KT> DiscriminatorOnColumnDSL<K, MF, S, T, KT> onColumn(final Predicate<? super K> discriminatorColumnPredicate, final Class<KT> discriminatorColunnType) {
			Function<List<K>, Getter<? super S, ? extends KT>> getterFactory = new DiscriminatorColumnValueFactory<KT>(discriminatorColumnPredicate, discriminatorColunnType);
			return new DiscriminatorOnColumnDSL<K, MF, S, T, KT>(mapperFactory, commonType, discriminatorColumnPredicate, getterFactory, discriminatorId);
		}
		public <KT> DiscriminatorOnColumnDSL<K, MF, S, T, KT> onColumn(String columnName, Class<KT> discriminatorColunnType) {
			return onColumn(CaseInsensitiveFieldKeyNamePredicate.of(columnName), discriminatorColunnType);
		}

		public <KT> DiscriminatorOnColumnDSL<K, MF, S, T, KT> onColumnWithNamedGetter(String columnName, CheckedBiFunction<S, String, KT> getter) {
			return onColumnWithNamedGetter(CaseInsensitiveFieldKeyNamePredicate.of(columnName), getter);
		}

		public <KT> DiscriminatorOnColumnDSL<K, MF, S, T, KT> onColumnWithNamedGetter(final Predicate<? super K> discriminatorColumnPredicate, final CheckedBiFunction<S, String, KT> getter) {
			Function<List<K>, Getter<? super S, ? extends KT>> getterFactory = new DiscriminatorNamedColumnValueFactory<KT>(discriminatorColumnPredicate, getter);
			return new DiscriminatorOnColumnDSL<K, MF, S, T, KT>(mapperFactory, commonType, discriminatorColumnPredicate, getterFactory, discriminatorId);

		}


		public <KT> DiscriminatorOnColumnDSL<K, MF, S, T, KT> onColumnWithIndexedGetter(String columnName, IndexedGetter<S, KT> getter) {
			return onColumnWithIndexedGetter(CaseInsensitiveFieldKeyNamePredicate.of(columnName), getter);
		}

		public <KT> DiscriminatorOnColumnDSL<K, MF, S, T, KT> onColumnWithIndexedGetter(final Predicate<? super K> discriminatorColumnPredicate, final IndexedGetter<S, KT> getter) {
			Function<List<K>, Getter<? super S, ? extends KT>> getterFactory = new Function<List<K>, Getter<? super S, ? extends KT>>() {
				@Override
				public Getter<? super S, ? extends KT> apply(List<K> ks) {
					if (ks.isEmpty()) throw new IllegalStateException("No discriminatory field found " + discriminatorColumnPredicate);
					if (ks.size() != 1) throw new IllegalStateException("Found multiple discriminator field " + ks);
					K k = ks.get(0);

					final int columnIndex = k.getIndex();

					return new Getter<S, KT>() {
						@Override
						public KT get(S target) throws Exception {
							return getter.get(target, columnIndex);
						}
					};
				}
			};
			return new DiscriminatorOnColumnDSL<K, MF, S, T, KT>(mapperFactory, commonType, discriminatorColumnPredicate, getterFactory, discriminatorId);

		}


		public  <KT> DiscriminatorOnColumnDSL<K, MF, S, T, KT> onGetter(final Getter<? super S, ? extends KT> getter) {
			Function<List<K>, Getter<? super S, ? extends KT>> getterFactory = new Function<List<K>, Getter<? super S, ? extends KT>>() {
				@Override
				public Getter<? super S, ? extends KT> apply(List<K> ks) {
					return getter;
				}
			};
			return new DiscriminatorOnColumnDSL<K, MF, S, T, KT>(mapperFactory, commonType, null, getterFactory, discriminatorId);
		}

		public MF with(Class<? extends T> implementation) {
			return with((Type)implementation);
		}

		public MF with(Type implementation) {
			DiscriminatorBuilder<S, K, T> db = new DiscriminatorBuilder<S, K, T>(commonType, mapperFactory.getReflectionService());
			db.defaultType(implementation);
			mapperFactory.discriminators.add(new MapperConfig.Discriminator<S, K, T>(commonType, db.cases.toArray(new MapperConfig.DiscriminatorCase[0]), ConstantPredicate.<K>truePredicate(), discriminatorId));
			return mapperFactory;
		}

		/*
		 * Backward compatible
		 */
		private MF _with(Consumer<DiscriminatorBuilder<S, K, T>> consumer) {
			DiscriminatorBuilder<S, K, T> db = new DiscriminatorBuilder<S, K, T>(commonType, mapperFactory.getReflectionService());
			consumer.accept(db);
			mapperFactory.discriminators.add(new MapperConfig.Discriminator<S, K, T>(commonType, db.cases.toArray(new MapperConfig.DiscriminatorCase[0]), ConstantPredicate.<K>truePredicate(), discriminatorId));
			return mapperFactory;
		}

		private class DiscriminatorColumnValueFactory<KT> implements Function<List<K>, Getter<? super S, ? extends KT>> {
			private final Predicate<? super K> discriminatorColumnPredicate;
			private final Class<KT> discriminatorColunnType;

			public DiscriminatorColumnValueFactory(Predicate<? super K> discriminatorColumnPredicate, Class<KT> discriminatorColunnType) {
				this.discriminatorColumnPredicate = discriminatorColumnPredicate;
				this.discriminatorColunnType = discriminatorColunnType;
			}

			@Override
			public Getter<? super S, ? extends KT> apply(List<K> ks) {
				if (ks.isEmpty()) throw new IllegalStateException("No discriminatory field found " + discriminatorColumnPredicate);
				if (ks.size() != 1) throw new IllegalStateException("Found multiple discriminator field " + ks);
				K k = ks.get(0);

				final ContextualGetter<? super S, KT> getter = mapperFactory.getterFactory.newGetter(discriminatorColunnType, k, null);

				return new Getter<S, KT>() {
					@Override
					public KT get(S target) throws Exception {
						return getter.get(target, null);
					}
				};
			}
		}

		private class DiscriminatorNamedColumnValueFactory<KT> implements Function<List<K>, Getter<? super S, ? extends KT>> {
			private final Predicate<? super K> discriminatorColumnPredicate;
			private final CheckedBiFunction<S, String, KT> getter;

			public DiscriminatorNamedColumnValueFactory(Predicate<? super K> discriminatorColumnPredicate, CheckedBiFunction<S, String, KT> getter) {
				this.discriminatorColumnPredicate = discriminatorColumnPredicate;
				this.getter = getter;
			}

			@Override
			public Getter<? super S, ? extends KT> apply(List<K> ks) {
				if (ks.isEmpty()) throw new IllegalStateException("No discriminatory field found " + discriminatorColumnPredicate);
				if (ks.size() != 1) throw new IllegalStateException("Found multiple discriminator field " + ks);
				K k = ks.get(0);

				final String columnName = k.getName();

				return new Getter<S, KT>() {
					@Override
					public KT get(S target) throws Exception {
						return getter.apply(target, columnName);
					}
				};
			}
		}
	}

	public static class DiscriminatorOnColumnDSL<K extends FieldKey<K>, MF extends AbstractMapperFactory<K, MF, S>, S, T, KT> {
		private final MF mapperFactory;
		private final Type commonType;
		private final Predicate<? super K> discriminatorColumnPredicate;
		private final Function<List<K>, Getter<? super S, ? extends KT>> getterFactory;
		private final Object discriminatorId;

		public DiscriminatorOnColumnDSL(MF mapperFactory, Type commonType, Predicate<? super K> discriminatorColumnPredicate, Function<List<K>, Getter<? super S, ? extends KT>> getterFactory, Object discriminatorId) {
			this.mapperFactory = mapperFactory;
			this.commonType = commonType;
			this.discriminatorColumnPredicate = discriminatorColumnPredicate;
			this.getterFactory = getterFactory;
			this.discriminatorId = discriminatorId;
		}

		public MF with(Consumer<DiscriminatorConditionBuilder<S, K, KT, T>> consumer) {
			if (discriminatorColumnPredicate != null) {
				mapperFactory.addColumnProperty(discriminatorColumnPredicate, OptionalProperty.INSTANCE, new DiscriminatorColumnProperty(commonType, discriminatorId));
			}

			DiscriminatorBuilder<S, K,  T> db = new DiscriminatorBuilder<S, K, T>(commonType, mapperFactory.getReflectionService());
			DiscriminatorConditionBuilder<S, K, KT, T> dcb = new DiscriminatorConditionBuilder<S, K, KT, T>(db, getterFactory);

			consumer.accept(dcb);

			mapperFactory.discriminators.add(new MapperConfig.Discriminator<S, K, T>(commonType, db.cases.toArray(new MapperConfig.DiscriminatorCase[0]), discriminatorColumnPredicate, discriminatorId));

			return mapperFactory;
		}
	}


}
