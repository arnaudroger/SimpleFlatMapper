package org.sfm.map;

import org.sfm.map.impl.*;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * JdbcMapperFactory allows you to customise the mappers and create an instance of it using a fluent syntax.
 * <p>
 * JdbcMapperFactory is not Thread-Safe but the mappers are.
 * It is strongly advised to instantiate one mapper per class for the life of your application.
 * <p>
 * You can instantiate dynamic mapper which will use the ResultSetMetaData
 * to figure out the list of the columns or a static one using a builder.
 * <p>
 * <code>
 *     // create a dynamic mapper targeting MyClass<br>
 *     JdbcMapperFactory<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newInstance()<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newMapper(MyClass.class);<br>
 *     <br>
 *     // create a static mapper targeting MyClass<br>
 *     JdbcMapperFactory<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newInstance()<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newBuilder(MyClass.class)<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("id")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("field1")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("field2")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.mapper();<br>
 *     <br>
 * </code>
 *
 */
// I don't really like using inheritance but did not see any other way
// to avoid rewriting a lot of delegate method...
@SuppressWarnings("unchecked")
public abstract class AbstractMapperFactory<K extends FieldKey<K>, CD extends ColumnDefinition<K, CD>, MF extends AbstractMapperFactory<K, CD, MF>> {


	private FieldMapperErrorHandler<K> fieldMapperErrorHandler = null;
    private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
    private RowHandlerErrorHandler rowHandlerErrorHandler = new RethrowRowHandlerErrorHandler();

    private final AbstractColumnDefinitionProvider<CD, K> columnDefinitions;
	private final CD identity;

	private boolean useAsm = true;
    private boolean disableAsm = false;
    private boolean failOnAsm = false;
    private int asmMapperNbFieldsLimit = MapperConfig.NO_ASM_MAPPER_THRESHOLD;

	private PropertyNameMatcherFactory propertyNameMatcherFactory = new DefaultPropertyNameMatcherFactory();

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
	 * Set the new MapperBuilderErrorHandler. the MapperBuilderErrorHandler is called when an error occurred or a property is not found in the builder while creating the mapper.
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

	protected final MapperConfig<K, CD> mapperConfig() {
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
     * change the number of fields threshold after which an asm mapper is not generated.
     * <p>
     * the default value is calculated from the benchmark results, currently 240.
     * @param asmMapperNbFieldsLimit the limit after which it does not use asm for the mapper.
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


    protected final <T> ClassMeta<T> getClassMeta(Type target) {
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
