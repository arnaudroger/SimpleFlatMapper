package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.IgnoreMapperBuilderErrorHandler;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.error.RethrowConsumerErrorHandler;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.map.PropertyNameMatcherFactory;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Map;


// I don't really like using inheritance but did not see any other way
// to avoid rewriting a lot of delegate method...
@SuppressWarnings("unchecked")
public abstract class AbstractMapperFactory<
		K extends FieldKey<K>,
		MF extends AbstractMapperFactory<K, MF>> {


	private FieldMapperErrorHandler<K> fieldMapperErrorHandler = null;
    private MapperBuilderErrorHandler mapperBuilderErrorHandler = RethrowMapperBuilderErrorHandler.INSTANCE;
    private ConsumerErrorHandler consumerErrorHandler = RethrowConsumerErrorHandler.INSTANCE;

    private final AbstractColumnDefinitionProvider<K> columnDefinitions;
	private final ColumnDefinition<K, ?> identity;

	private boolean useAsm = true;
    private boolean failOnAsm = false;
    private int asmMapperNbFieldsLimit = MapperConfig.NO_ASM_MAPPER_THRESHOLD;

	private PropertyNameMatcherFactory propertyNameMatcherFactory = DefaultPropertyNameMatcherFactory.DEFAULT;

    private ReflectionService reflectionService = null;
	private int maxMethodSize = MapperConfig.MAX_METHOD_SIZE;
	private boolean assumeInjectionModifiesValues;


	public AbstractMapperFactory(AbstractMapperFactory<K, ?> config) {
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
	}


	public AbstractMapperFactory(AbstractColumnDefinitionProvider<K> columnDefinitions, ColumnDefinition<K, ?> identity) {
		this.columnDefinitions = columnDefinitions;
		this.identity = identity;
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
     * Change the mapperBuilderErrorHandler to an IgnoreMapperBuilderErrorHandler.
     * @return the current factory
     */
    public final MF ignorePropertyNotFound() {
        return mapperBuilderErrorHandler(IgnoreMapperBuilderErrorHandler.INSTANCE);
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

	public final MapperConfig<K> mapperConfig() {
		return MapperConfig
				.<K>config(enrichColumnDefinitions(columnDefinitions))
				.mapperBuilderErrorHandler(mapperBuilderErrorHandler)
				.propertyNameMatcherFactory(propertyNameMatcherFactory)
				.failOnAsm(failOnAsm)
				.asmMapperNbFieldsLimit(asmMapperNbFieldsLimit)
				.fieldMapperErrorHandler(fieldMapperErrorHandler)
				.consumerErrorHandler(consumerErrorHandler)
				.maxMethodSize(maxMethodSize)
				.assumeInjectionModifiesValues(assumeInjectionModifiesValues);
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

}
