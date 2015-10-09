package org.sfm.map.mapper;

import org.sfm.map.*;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.error.RethrowMapperBuilderErrorHandler;
import org.sfm.map.error.RethrowRowHandlerErrorHandler;
import org.sfm.map.impl.*;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
import org.sfm.utils.ConstantUnaryFactory;
import org.sfm.utils.Predicate;
import org.sfm.utils.UnaryFactory;

import java.lang.reflect.Type;
import java.util.Map;


// I don't really like using inheritance but did not see any other way
// to avoid rewriting a lot of delegate method...
@SuppressWarnings("unchecked")
public abstract class AbstractMapperFactory<
		K extends FieldKey<K>,
		CD extends ColumnDefinition<K, CD>,
		MF extends AbstractMapperFactory<K, CD, MF>> {


	private FieldMapperErrorHandler<K> fieldMapperErrorHandler = null;
    private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
    private RowHandlerErrorHandler rowHandlerErrorHandler = new RethrowRowHandlerErrorHandler();

    private final AbstractColumnDefinitionProvider<CD, K> columnDefinitions;
	private final CD identity;

	private boolean useAsm = true;
    private boolean disableAsm = false;
    private boolean failOnAsm = false;
    private int asmMapperNbFieldsLimit = MapperConfig.NO_ASM_MAPPER_THRESHOLD;

	private PropertyNameMatcherFactory propertyNameMatcherFactory = DefaultPropertyNameMatcherFactory.DEFAULT;

    private ReflectionService reflectionService = null;
	private int maxMethodSize = MapperConfig.MAX_METHOD_SIZE;

	public AbstractMapperFactory(AbstractColumnDefinitionProvider<CD, K> columnDefinitions, CD identity) {
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
        this.mapperBuilderErrorHandler = new IgnoreMapperBuilderErrorHandler();
        return (MF) this;
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
     * the RowHandlerErrorHandler is called when an exception is thrown by the RowHandler in the forEach call.
     * @param rowHandlerErrorHandler the new RowHandlerErrorHandler
     * @return the current factory
     */
	public final MF rowHandlerErrorHandler(final RowHandlerErrorHandler rowHandlerErrorHandler) {
		this.rowHandlerErrorHandler = rowHandlerErrorHandler;
		return (MF) this;
	}

	/**
	 *
	 * @param useAsm false if you want to disable asm generation of Mappers, Getter and Setter. This would be active by default if asm is present in a compatible version.
	 * @return the current factory
	 */
	public final MF useAsm(final boolean useAsm) {
		this.useAsm = useAsm;
		return (MF) this;
	}

	/**
	 * @param disableAsm true if you want to disable asm for generation and to resolve constructor parameter names.
     * @return the current factory
	 */
	public final MF disableAsm(final boolean disableAsm) {
		this.disableAsm = disableAsm;
		return (MF) this;
	}


    /**
     * Override the default implementation of the ReflectionService.
     * @param reflectionService the overriding instance
     * @return the current factory
     */
    public final MF reflectionService(final ReflectionService reflectionService) {
        this.reflectionService = reflectionService;
        return (MF) this;
    }

	public final MapperConfig<K, CD> mapperConfig() {
		return MapperConfig
				.<K, CD>config(columnDefinitions)
				.mapperBuilderErrorHandler(mapperBuilderErrorHandler)
				.propertyNameMatcherFactory(propertyNameMatcherFactory)
				.failOnAsm(failOnAsm)
				.asmMapperNbFieldsLimit(asmMapperNbFieldsLimit)
				.fieldMapperErrorHandler(fieldMapperErrorHandler)
				.rowHandlerErrorHandler(rowHandlerErrorHandler)
				.maxMethodSize(maxMethodSize);
	}

	/**
	 * Associate an alias on the column key to rename to value.
	 * @param key the column to rename
	 * @param value then name to rename to
	 * @return the current factory
	 */
	public final MF addAlias(String key, String value) {
		return addColumnDefinition(key,  identity.addRename(value));
	}

    /**
     * Associate the specified columnDefinition to the specified column.
     * @param key the column
     * @param columnDefinition the columnDefinition
     * @return the current factory
     */
	public final MF addColumnDefinition(String key, CD columnDefinition) {
		return addColumnDefinition(new CaseInsensitiveFieldKeyNamePredicate(key), columnDefinition);
	}

    /**
     * Associate the specified columnDefinition to the column matching the predicate.
     * @param predicate the column predicate
     * @param columnDefinition the columnDefinition
     * @return the current factory
     */
	public final MF addColumnDefinition(Predicate<? super K> predicate, CD columnDefinition) {
		columnDefinitions.addColumnDefinition(predicate, columnDefinition);
		return (MF) this;
	}

	/**
	 * Associate the specified columnProperties to the column matching the predicate.
	 * @param name the column predicate
	 * @param properties the properties
	 * @return the current factory
	 */
	public final MF addColumnProperty(String name, ColumnProperty... properties) {
		for(ColumnProperty property : properties) {
			columnDefinitions.addColumnProperty(new CaseInsensitiveFieldKeyNamePredicate(name), new ConstantUnaryFactory<K, ColumnProperty>(property));
		}
		return (MF) this;
	}

	/**
	 * Associate the specified columnProperties to the column matching the predicate.
	 * @param predicate the column predicate
	 * @param properties the properties
	 * @return the current factory
	 */
	public final MF addColumnProperty(Predicate<? super K> predicate, ColumnProperty... properties) {
		for(ColumnProperty property : properties) {
			columnDefinitions.addColumnProperty(predicate, new ConstantUnaryFactory<K, ColumnProperty>(property));
		}
		return (MF) this;
	}

	/**
	 * Associate the specified columnProperties to the column matching the predicate.
	 * @param predicate the column predicate
	 * @param propertyFactory the properties
	 * @return the current factory
	 */
	public final MF addColumnProperty(Predicate<? super K> predicate, UnaryFactory<K, ColumnProperty> propertyFactory) {
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
     * Associate the aliases value to the column key.
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
     * @return the current RowHandlerErrorHandler
     */
    public final RowHandlerErrorHandler rowHandlerErrorHandler() {
        return rowHandlerErrorHandler;
    }


    public final <T> ClassMeta<T> getClassMeta(Type target) {
        return getReflectionService().getClassMeta(target);
    }

    private ReflectionService getReflectionService() {
        if (reflectionService != null) {
            return reflectionService;
        } else {
            return ReflectionService.newInstance(disableAsm, useAsm);
        }
    }
}
