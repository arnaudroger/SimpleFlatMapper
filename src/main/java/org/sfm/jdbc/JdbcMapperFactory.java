package org.sfm.jdbc;

import org.sfm.jdbc.impl.DynamicJdbcMapper;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.*;
import org.sfm.map.impl.*;
import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
public final class JdbcMapperFactory {


    /**
	 * instantiate a new JdbcMapperFactory
	 * @return a new instance JdbcMapperFactory
	 */
	public static JdbcMapperFactory newInstance() {
		return new JdbcMapperFactory();
	}

	private FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler = null;

    private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
    private RowHandlerErrorHandler rowHandlerErrorHandler = new RethrowRowHandlerErrorHandler();
    private FieldMapperColumnDefinitionProviderImpl<JdbcColumnKey, ResultSet> columnDefinitions = new FieldMapperColumnDefinitionProviderImpl<JdbcColumnKey, ResultSet>();
	private PropertyNameMatcherFactory propertyNameMatcherFactory = new DefaultPropertyNameMatcherFactory();

    private GetterFactory<ResultSet, JdbcColumnKey> getterFactory = new ResultSetGetterFactory();
	private boolean useAsm = true;

    private boolean disableAsm = false;
    private boolean failOnAsm = false;
    private int asmMapperNbFieldsLimit = FieldMapperMapperBuilder.NO_ASM_MAPPER_THRESHOLD;

    private ReflectionService reflectionService = null;

	private JdbcMapperFactory() {
	}

    /**
     * the FieldMapperErrorHandler is called when a error occurred when mapping a field from the source to the target.
     * By default it just throw the Exception.
     * @param fieldMapperErrorHandler the new FieldMapperErrorHandler
     * @return the current factory
     */
	public JdbcMapperFactory fieldMapperErrorHandler(final FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler) {
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		return this;
	}

    /**
     * Change the mapperBuilderErrorHandler to an IgnoreMapperBuilderErrorHandler.
     * @return the current factory
     */
    public JdbcMapperFactory ignorePropertyNotFound() {
        this.mapperBuilderErrorHandler = new IgnoreMapperBuilderErrorHandler();
        return this;
    }

    /**
	 * Set the new MapperBuilderErrorHandler. the MapperBuilderErrorHandler is called when an error occurred or a property is not found in the builder while creating the mapper.
	 * @param mapperBuilderErrorHandler the MapperBuilderErrorHandler
	 * @return the current factory
	 */
	public JdbcMapperFactory mapperBuilderErrorHandler(final MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		return this;
	}


    /**
     * the RowHandlerErrorHandler is called when an exception is thrown by the RowHandler in the forEach call.
     * @param rowHandlerErrorHandler the new RowHandlerErrorHandler
     * @return the current factory
     */
	public JdbcMapperFactory rowHandlerErrorHandler(final RowHandlerErrorHandler rowHandlerErrorHandler) {
		this.rowHandlerErrorHandler = rowHandlerErrorHandler;
		return this;
	}

	/**
	 * 
	 * @param useAsm false if you want to disable asm generation of Mappers, Getter and Setter. This would be active by default if asm is present in a compatible version.
	 * @return the current factory
	 */
	public JdbcMapperFactory useAsm(final boolean useAsm) {
		this.useAsm = useAsm;
		return this;
	}
	
	/**
	 * @param disableAsm true if you want to disable asm for generation and to resolve constructor parameter names.
     * @return the current factory
	 */
	public JdbcMapperFactory disableAsm(final boolean disableAsm) {
		this.disableAsm = disableAsm;
		return this;
	}

    /**
     * Override the default implementation of the GetterFactory used to get access to value from the ResultSet.
     * @param getterFactory the getterFactory
     * @return the current factory
     */
	public JdbcMapperFactory getterFactory(final GetterFactory<ResultSet, JdbcColumnKey> getterFactory) {
		this.getterFactory = getterFactory;
		return this;
	}

    /**
     * Override the default implementation of the ReflectionService.
     * @param reflectionService the overriding instance
     * @return the current factory
     */
    public JdbcMapperFactory reflectionService(final ReflectionService reflectionService) {
        this.reflectionService = reflectionService;
        return this;
    }


	/**
	 * Will create a instance of JdbcMapper based on the specified metadata and the target class.
	 * @param target the target class of the mapper
	 * @param metaData the metadata to create the mapper from
     * @param <T> the mapper target type
	 * @return a mapper that will map the data represented by the metadata to an instance of target
     * @throws java.sql.SQLException if an error occurs getting the metaData
	 */
	public <T> JdbcMapper<T> newMapper(final Class<T> target, final ResultSetMetaData metaData) throws SQLException {
		JdbcMapperBuilder<T> builder = newBuilder(target);
		builder.addMapping(metaData);
		return builder.mapper();
	}
	
	/**
	 * Will create a instance of JdbcMapperBuilder on the specified target class.
	 * @param target the target class
     * @param <T> the mapper target type
	 * @return the builder
	 */
	public <T> JdbcMapperBuilder<T> newBuilder(final Class<T> target) {
		return newBuilder((Type)target);
	}

    /**
     * Will create a instance of JdbcMapperBuilder on the type T specified by the typeReference.
     * @param target the typeReference
     * @param <T> the mapper target type
     * @return the builder
     */
    public <T> JdbcMapperBuilder<T> newBuilder(final TypeReference<T> target) {
        return newBuilder(target.getType());
    }

    /**
     * Will create a instance of JdbcMapperBuilder on the specified type.
     * @param target the type
     * @param <T> the mapper target type
     * @return the builder
     */
    public <T> JdbcMapperBuilder<T> newBuilder(final Type target) {
		ClassMeta<T> classMeta = getClassMeta(target);

		JdbcMapperBuilder<T> builder =
                new JdbcMapperBuilder<T>(classMeta, mapperBuilderErrorHandler, columnDefinitions,
                        propertyNameMatcherFactory, getterFactory, failOnAsm, asmMapperNbFieldsLimit,
                        new JdbcMappingContextFactoryBuilder());
		
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.jdbcMapperErrorHandler(rowHandlerErrorHandler);
		return builder;
	}

	/**
	 * Will create a DynamicMapper on the specified target class.
	 * @param target the class
     * @param <T> the mapper target type
     * @return the DynamicMapper
	 */
	public <T> JdbcMapper<T> newMapper(final Class<T> target) {
		return newMapper((Type)target);
	}

    /**
     * Will create a DynamicMapper on the type specified by the TypeReference.
     * @param target the TypeReference
     * @param <T> the mapper target type
     * @return the DynamicMapper
     */
    public <T> JdbcMapper<T> newMapper(final TypeReference<T> target) {
        return newMapper(target.getType());
    }

    /**
     * Will create a DynamicMapper on the specified type.
     * @param target the type
     * @param <T> the mapper target type
     * @return the DynamicMapper
     */
	public <T> JdbcMapper<T> newMapper(final Type target) {
		ClassMeta<T> classMeta = getClassMeta(target);
		return new DynamicJdbcMapper<T>(classMeta, fieldMapperErrorHandler, mapperBuilderErrorHandler,
                rowHandlerErrorHandler, columnDefinitions, propertyNameMatcherFactory, failOnAsm, asmMapperNbFieldsLimit);
	}


	/**
	 * Associate an alias on the column key to rename to value.
	 * @param key the column to rename
	 * @param value then name to rename to
	 * @return the current factory
	 */
	public JdbcMapperFactory addAlias(String key, String value) {
		return addColumnDefinition(key, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>renameDefinition(value));
	}

	/**
	 * Associate the specified FieldMapper for the specified column.
	 * @param key the column
	 * @param fieldMapper the fieldMapper
	 * @return the current factory
	 */
	public JdbcMapperFactory addCustomFieldMapper(String key, FieldMapper<ResultSet, ?> fieldMapper) {
		return addColumnDefinition(key, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>customFieldMapperDefinition(fieldMapper));
	}

    /**
     * Associate the specified Getter for the specified column.
     * @param key the column
     * @param getter the getter
     * @return the current factory
     */
	public JdbcMapperFactory addCustomGetter(String key, Getter<ResultSet, ?> getter) {
		return addColumnDefinition(key, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>customGetter(getter));
	}

    /**
     * Associate the specified columnDefinition to the specified column.
     * @param key the column
     * @param columnDefinition the columnDefinition
     * @return the current factory
     */
	public JdbcMapperFactory addColumnDefinition(String key, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
		return addColumnDefinition(new CaseInsensitiveFieldKeyNamePredicate(key), columnDefinition);
	}

    /**
     * Associate the specified columnDefinition to the column matching the predicate.
     * @param predicate the column predicate
     * @param columnDefinition the columnDefinition
     * @return the current factory
     */
	public JdbcMapperFactory addColumnDefinition(Predicate<? super JdbcColumnKey> predicate, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
		columnDefinitions.addColumnDefinition(predicate, columnDefinition);
		return this;
	}

    /**
     * Override the default PropertyNameMatcherFactory with the specified factory.
     * @param propertyNameMatcherFactory the factory
     * @return the current factory
     */
	public JdbcMapperFactory propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		return this;
	}

    /**
     * Associate the aliases value to the column key.
     * @param aliases the key value pair
     * @return the current factory
     */
    public JdbcMapperFactory addAliases(Map<String, String> aliases) {
		for(Map.Entry<String, String> e : aliases.entrySet()) {
			addAlias(e.getKey(), e.getValue());
		}
		return this;
	}

    /**
     * @param b true if we want the builder to fail on asm generation failure
     * @return the current factory
     */
    public JdbcMapperFactory failOnAsm(boolean b) {
        this.failOnAsm = b;
        return this;
    }

    /**
     * change the number of fields threshold after which an asm mapper is not generated.
     * <p>
     * the default value is calculated from the benchmark results, currently 240.
     * @param asmMapperNbFieldsLimit the limit after which it does not use asm for the mapper.
     * @return the factory
     */
    public JdbcMapperFactory asmMapperNbFieldsLimit(final int asmMapperNbFieldsLimit) {
        this.asmMapperNbFieldsLimit = asmMapperNbFieldsLimit;
        return this;
    }

    /**
     * Mark the specified columns as keys.
     * @param columns the columns
     * @return  the current factory
     */
    public JdbcMapperFactory addKeys(String... columns) {
        for(String col : columns) {
            addColumnDefinition(col, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>key());
        }
        return this;
    }

    /**
     * Create a discriminator builder based on the specified column
     * @param column the discriminator column
     * @param <T> the root type of the mapper
     * @return a builder to specify the type mapping
     */
    public <T> DiscriminatorJdbcBuilder<T> newDiscriminator(String column) {
        ignorePropertyNotFound();
        addColumnDefinition(column, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>ignoreDefinition());
        return new DiscriminatorJdbcBuilder<T>(column, this);
    }

    /**
     * @return the current RowHandlerErrorHandler
     */
    public RowHandlerErrorHandler rowHandlerErrorHandler() {
        return rowHandlerErrorHandler;
    }


    private <T> ClassMeta<T> getClassMeta(Type target) {
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
