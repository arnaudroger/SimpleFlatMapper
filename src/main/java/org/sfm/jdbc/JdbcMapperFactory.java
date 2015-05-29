package org.sfm.jdbc;

import org.sfm.jdbc.impl.DynamicJdbcMapper;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.*;
import org.sfm.map.impl.*;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

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
public final class JdbcMapperFactory
		extends AbstractMapperFactory<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>, JdbcMapperFactory> {


    /**
	 * instantiate a new JdbcMapperFactory
	 * @return a new instance JdbcMapperFactory
	 */
	public static JdbcMapperFactory newInstance() {
		return new JdbcMapperFactory();
	}


    private GetterFactory<ResultSet, JdbcColumnKey> getterFactory = new ResultSetGetterFactory();

	private JdbcMapperFactory() {
		super(new FieldMapperColumnDefinitionProviderImpl<JdbcColumnKey, ResultSet>(), FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>identity());
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
                new JdbcMapperBuilder<T>(
						classMeta,
						mapperConfig(),
						getterFactory,
                        new JdbcMappingContextFactoryBuilder());
		
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
		return new DynamicJdbcMapper<T>(classMeta, mapperConfig());
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
}
