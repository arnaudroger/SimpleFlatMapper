package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.*;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.error.RethrowConsumerErrorHandler;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.map.getter.ComposedContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.property.IgnoreProperty;
import org.simpleflatmapper.map.property.OptionalProperty;
import org.simpleflatmapper.map.property.RenameProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
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
    private final List<MapperConfig.Discriminator<S, ?>> discriminators = new ArrayList<MapperConfig.Discriminator<S, ?>>();
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
		for(Object property : properties) {
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
	public final MF addColumnPropertyForType(Type type, Predicate<? super K> keyPredicate, UnaryFactory<K, Object> propertyFactory) {
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

	public MF addGetterForType(final Predicate<Type> typePredicate, final Function<K, ContextualGetter<S, ?>> getterFactory) {
		return addGetterFactory(new ContextualGetterFactory<S, K>() {
			@Override
			public <P> ContextualGetter<S, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, K> mappingContextFactoryBuilder, Object... properties) {
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
			public <P> ContextualGetter<S, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, K> mappingContextFactoryBuilder, Object... properties) {
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

	public <T> MF discriminator(Type commonType, Consumer<DiscriminatorBuilder<S, T>> consumer) {
		DiscriminatorBuilder<S, T> db = new DiscriminatorBuilder<S, T>(commonType, getReflectionService());
		
		consumer.accept(db);
		
		discriminators.add(new MapperConfig.Discriminator<S, T>(commonType, db.cases.toArray(new MapperConfig.DiscriminatorCase[0])));
    	
    	return (MF) this;
	}

	public <T, V> MF discriminator(Type commonType, Getter<? super S, ? extends V> getter, Consumer<DiscriminatorConditionBuilder<S, V, T>> consumer) {
		DiscriminatorBuilder<S, T> db = new DiscriminatorBuilder<S, T>(commonType, getReflectionService());
		DiscriminatorConditionBuilder<S, V, T> dcb = new DiscriminatorConditionBuilder<S, V, T>(db, getter);
		
		consumer.accept(dcb);

		discriminators.add(new MapperConfig.Discriminator<S, T>(commonType, db.cases.toArray(new MapperConfig.DiscriminatorCase[0])));

		return (MF) this;
	}
	public <T> MF discriminator(Class<T> commonType, Consumer<DiscriminatorBuilder<S, T>> consumer) {
    	return discriminator((Type)commonType, consumer);
	}

	public <T, V> MF discriminator(Class<T> commonType, Getter<? super S, ? extends V> getter, Consumer<DiscriminatorConditionBuilder<S, V, T>> consumer) {
		return discriminator((Type)commonType, getter, consumer);
	}

	public <T, V> MF discriminator(Class<T> commonType, final String discriminatorColumn, CheckedBiFunction<S, String, V> discriminatorFieldAccessor, Consumer<DiscriminatorConditionBuilder<S, V, T>> consumer) {
		return discriminator((Type) commonType, discriminatorColumn, discriminatorFieldAccessor, consumer);
	}

	public <T, V> MF discriminator(Type commonType, final String discriminatorColumn, final CheckedBiFunction<S, String, V> discriminatorFieldAccessor, Consumer<DiscriminatorConditionBuilder<S, V, T>> consumer) {
		addColumnProperty(discriminatorColumn, OptionalProperty.INSTANCE);
		Getter<? super S, ? extends V> getter = new Getter<S, V>() {
			@Override
			public V get(S target) throws Exception {
				return discriminatorFieldAccessor.apply(target, discriminatorColumn);
			}
		};
		return discriminator(commonType, getter, consumer);
	}

	public static class DiscriminatorConditionBuilder<S, V, T> {
    	private final DiscriminatorBuilder<S, T> discriminatorBuilder;
    	private final Getter<? super S, ? extends V> getter;

		public DiscriminatorConditionBuilder(DiscriminatorBuilder<S, T> discriminatorBuilder, Getter<? super S, ? extends V> getter) {
			this.discriminatorBuilder = discriminatorBuilder;
			this.getter = getter;
		}

		public DiscriminatorConditionBuilder<S, V, T> when(V value, Type type) {
			return discriminatorCase(value, type);
		}
		public DiscriminatorConditionBuilder<S, V, T> when(V value, Class<T> type) {
			return discriminatorCase(value, type);
		}
		public DiscriminatorConditionBuilder<S, V, T> when(V value, ClassMeta<? extends T> classMeta) {
			return discriminatorCase(value, classMeta);
		}

		public DiscriminatorConditionBuilder<S, V, T> when(Predicate<V> predicate, Type type) {
			return discriminatorCase(predicate, type);
		}
		
		public DiscriminatorConditionBuilder<S, V, T> when(Predicate<V> predicate, Class<T> type) {
			return discriminatorCase(predicate, type);
		}
		public DiscriminatorConditionBuilder<S, V, T> when(Predicate<V> predicate, ClassMeta<? extends T> classMeta) {
			return discriminatorCase(predicate, classMeta);
		}
		
		public DiscriminatorConditionBuilder<S, V, T> discriminatorCase(V value, Type type) {
			return discriminatorCase(toEqualsPredicate(value), type);
		}
		public DiscriminatorConditionBuilder<S, V, T> discriminatorCase(V value, Class<T> type) {
			return discriminatorCase(toEqualsPredicate(value), type);
		}
		public DiscriminatorConditionBuilder<S, V, T> discriminatorCase(V value, ClassMeta<? extends T> classMeta) {
			return discriminatorCase(toEqualsPredicate(value), classMeta);
		}

		public DiscriminatorConditionBuilder<S, V, T> discriminatorCase(Predicate<V> predicate, Type type) {
			discriminatorBuilder.when(toSourcePredicate(predicate), type);
			return this;
		}
		public DiscriminatorConditionBuilder<S, V, T> discriminatorCase(Predicate<V> predicate, Class<T> type) {
			discriminatorBuilder.when(toSourcePredicate(predicate), type);
			return this;
		}
		public DiscriminatorConditionBuilder<S, V, T> discriminatorCase(Predicate<V> predicate, ClassMeta<? extends T> classMeta) {
			discriminatorBuilder.when(toSourcePredicate(predicate), classMeta);
			return this;
		}

		private Predicate<V> toEqualsPredicate(V value) {
			return EqualsPredicate.<V>of(value);
		}
		
		private Predicate<S> toSourcePredicate(Predicate<V> predicate) {
			return new SourcePredicate<S, V>(predicate, getter);
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

	public static final class DiscriminatorBuilder<S, T> {

		private final Type commonType;
		private final ReflectionService reflectionService;

		List<MapperConfig.DiscriminatorCase<S, T>> cases = new ArrayList<MapperConfig.DiscriminatorCase<S, T>>();
		
		public DiscriminatorBuilder(Type type, ReflectionService reflectionService) {
			this.commonType = type;
			this.reflectionService = reflectionService;
		}


		public DiscriminatorBuilder<S, T> when(Predicate<S> predicate, ClassMeta<? extends T> classMeta) {
			return discriminatorCase(predicate, classMeta);
		}

		public DiscriminatorBuilder<S, T> when(Predicate<S> predicate, Class<? extends T> target) {
			return discriminatorCase(predicate, target);
		}
		public DiscriminatorBuilder<S, T> when(Predicate<S> predicate, Type target) {
			return discriminatorCase(predicate, target);
		}
		
		public DiscriminatorBuilder<S, T> discriminatorCase(Predicate<S> predicate, ClassMeta<? extends T> classMeta) {
			MapperConfig.DiscriminatorCase<S, T> dCase = new MapperConfig.DiscriminatorCase<S, T>(predicate, classMeta);
			cases.add(dCase);
			return this;
		}
		
		public DiscriminatorBuilder<S, T> discriminatorCase(Predicate<S> predicate, Class<? extends T> target) {
			return discriminatorCase(predicate, reflectionService.getClassMeta(target));
		}
		public DiscriminatorBuilder<S, T> discriminatorCase(Predicate<S> predicate, Type target) {
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
}
