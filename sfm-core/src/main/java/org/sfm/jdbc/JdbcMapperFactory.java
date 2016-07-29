package org.sfm.jdbc;

import org.sfm.jdbc.impl.PreparedStatementSetterFactory;
import org.sfm.map.*;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.*;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.JpaAliasProvider;
import org.sfm.utils.UnaryFactory;
import org.sfm.utils.UnaryFactoryWithException;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * JdbcMapperFactory allows you to customise the mappers and create an instance of it using a fluent syntax.
 * <p>
 * JdbcMapperFactory is not Thread-Safe but the mappers are.
 * It is strongly advised to instantiate one jdbcMapper per class for the life of your application.
 * <p>
 * You can instantiate dynamic jdbcMapper which will use the ResultSetMetaData
 * to figure out the list of the columns or a static one using a builder.
 * <p>
 * <code>
 *     // create a dynamic jdbcMapper targeting MyClass<br>
 *     JdbcMapperFactory<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newInstance()<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newMapper(MyClass.class);<br>
 *     <br>
 *     // create a static jdbcMapper targeting MyClass<br>
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
		extends AbstractMapperFactory<JdbcColumnKey,
                FieldMapperColumnDefinition<JdbcColumnKey>,
                JdbcMapperFactory> {

	static {
		JpaAliasProvider.registers();
	}

    /**
	 * instantiate a new JdbcMapperFactory
	 * @return a new instance JdbcMapperFactory
	 */
	public static JdbcMapperFactory newInstance() {
		return new JdbcMapperFactory();
	}

	public static JdbcMapperFactory newInstance(
			AbstractMapperFactory<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>, ?> config) {
		return new JdbcMapperFactory(config);
	}

	private GetterFactory<ResultSet, JdbcColumnKey> getterFactory = new ResultSetGetterFactory();

	private JdbcMapperFactory(AbstractMapperFactory<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>, ?> config) {
		super(config);
	}

	private JdbcMapperFactory() {
		super(new FieldMapperColumnDefinitionProviderImpl<JdbcColumnKey>(), FieldMapperColumnDefinition.<JdbcColumnKey>identity());
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
	 * Associate the specified FieldMapper for the specified column.
	 * @param key the column
	 * @param fieldMapper the fieldMapper
	 * @return the current factory
	 */
	public JdbcMapperFactory addCustomFieldMapper(String key, FieldMapper<ResultSet, ?> fieldMapper) {
		return addColumnDefinition(key, FieldMapperColumnDefinition.<JdbcColumnKey>customFieldMapperDefinition(fieldMapper));
	}

	/**
	 * Associate the specified Getter for the specified column.
	 * @param key the column
	 * @param getter the getter
	 * @return the current factory
	 */
	public JdbcMapperFactory addCustomGetter(String key, Getter<ResultSet, ?> getter) {
		return addColumnDefinition(key, FieldMapperColumnDefinition.<JdbcColumnKey>customGetter(getter));
	}

	/**
	 * Will create a instance of JdbcMapper based on the specified metadata and the target class.
	 * @param target the target class of the jdbcMapper
	 * @param metaData the metadata to create the jdbcMapper from
     * @param <T> the jdbcMapper target type
	 * @return a jdbcMapper that will map the data represented by the metadata to an instance of target
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
     * @param <T> the jdbcMapper target type
	 * @return the builder
	 */
	public <T> JdbcMapperBuilder<T> newBuilder(final Class<T> target) {
		return newBuilder((Type)target);
	}

    /**
     * Will create a instance of JdbcMapperBuilder on the type T specified by the typeReference.
     * @param target the typeReference
     * @param <T> the jdbcMapper target type
     * @return the builder
     */
    public <T> JdbcMapperBuilder<T> newBuilder(final TypeReference<T> target) {
        return newBuilder(target.getType());
    }

    /**
     * Will create a instance of JdbcMapperBuilder on the specified type.
     * @param target the type
     * @param <T> the jdbcMapper target type
     * @return the builder
     */
    public <T> JdbcMapperBuilder<T> newBuilder(final Type target) {
		ClassMeta<T> classMeta = getClassMeta(target);
		return newBuilder(classMeta);
	}

	public <T> JdbcMapperBuilder<T> newBuilder(ClassMeta<T> classMeta) {
		return new JdbcMapperBuilder<T>(
						classMeta,
						mapperConfig(),
						getterFactory,
                        new JdbcMappingContextFactoryBuilder());
	}

	/**
	 *
	 * @param target the type
	 * @param <T> the type
	 * @return a builder to create a mapper from target to PreparedStatement
	 */
	public <T> PreparedStatementMapperBuilder<T> buildFrom(final Class<T> target) {
		return buildFrom((Type) target);
	}

	public <T> PreparedStatementMapperBuilder<T> buildFrom(final Type target) {
		ClassMeta<T> classMeta = getClassMeta(target);
		return buildFrom(classMeta);
	}

	public <T> PreparedStatementMapperBuilder<T> buildFrom(final TypeReference<T> target) {
		return buildFrom(target.getType());
	}

	public <T> PreparedStatementMapperBuilder<T> buildFrom(final ClassMeta<T> classMeta) {
		return new PreparedStatementMapperBuilder<T>(classMeta, mapperConfig(), ConstantTargetFieldMapperFactorImpl.instance(new PreparedStatementSetterFactory()));
	}

	public <T> PreparedStatementMapperBuilder<T> from(final Class<T> target) {
		return buildFrom(target);
	}

	public <T> PreparedStatementMapperBuilder<T> from(final Type target) {
		return buildFrom(target);
	}

	public <T> PreparedStatementMapperBuilder<T> from(final TypeReference<T> target) {
		return buildFrom(target);
	}

	public <T> PreparedStatementMapperBuilder<T> from(final ClassMeta<T> classMeta) {
		return buildFrom(classMeta);
	}


	/**
	 * Will create a DynamicMapper on the specified target class.
	 * @param target the class
	 * @param <T> the jdbcMapper target type
	 * @return the DynamicMapper
	 */
	public <T> JdbcMapper<T> newMapper(final Class<T> target) {
		return newMapper((Type) target);
	}

    /**
     * Will create a DynamicMapper on the type specified by the TypeReference.
     * @param target the TypeReference
     * @param <T> the jdbcMapper target type
     * @return the DynamicMapper
     */
    public <T> JdbcMapper<T> newMapper(final TypeReference<T> target) {
        return newMapper(target.getType());
    }

	public <T, K> CrudDSL<T, K> crud(final Type target, final Type keyTarget) {
		return crud(this.<T>getClassMeta(target), this.<K>getClassMeta(TypeHelper.toBoxedClass(keyTarget)));
	}

	public <T, K> CrudDSL<T, K> crud(final ClassMeta<T> target, final ClassMeta<K> keyTarget) {
		return new CrudDSL<T, K>(target, keyTarget, JdbcMapperFactory.newInstance(this));
	}

	public <T, K> CrudDSL<T, K> crud(final Class<T> target, final Class<K> keyTarget) {
		return crud((Type)target, (Type)keyTarget);
	}

	/**
     * Will create a DynamicMapper on the specified type.
     * @param target the type
     * @param <T> the jdbcMapper target type
     * @return the DynamicMapper
     */
	public <T> JdbcMapper<T> newMapper(final Type target) {
		final ClassMeta<T> classMeta = getClassMeta(target);
		return new DynamicJdbcSetRowMapper<T>(new SetRowMapperFactory<T>(classMeta),  new MapperKeyFactory(),  new MapperKeyFactory());
	}

	public static class DynamicJdbcSetRowMapper<T>
			extends DynamicSetRowMapper<ResultSet, ResultSet, T, SQLException, JdbcColumnKey>
			implements DynamicJdbcMapper<T> {

		public DynamicJdbcSetRowMapper(
				UnaryFactory<MapperKey<JdbcColumnKey>, SetRowMapper<ResultSet, ResultSet, T, SQLException>> mapperFactory,
				UnaryFactoryWithException<ResultSet, MapperKey<JdbcColumnKey>, SQLException> mapperKeyFromRow,
				UnaryFactoryWithException<ResultSet, MapperKey<JdbcColumnKey>, SQLException> mapperKeyFromSet) {
			super(mapperFactory, mapperKeyFromRow, mapperKeyFromSet, MapperKeyComparator.jdbcColumnKeyComparator());
		}


		@Override
		public JdbcMapper<T> getMapper(ResultSetMetaData metaData) throws SQLException {
			return (JdbcMapper<T>) getMapper(JdbcColumnKey.mapperKey(metaData));
		}

		@Override
		public String toString() {
			return "DynamicJdbcSetRowMapper{}";
		}
	}

	/**
     * Create a discriminator builder based on the specified column
     * @param column the discriminator column
     * @param <T> the root type of the jdbcMapper
     * @return a builder to specify the type mapping
     */
    public <T> DiscriminatorJdbcBuilder<T> newDiscriminator(String column) {
        ignorePropertyNotFound();
        addColumnDefinition(column, FieldMapperColumnDefinition.<JdbcColumnKey>ignoreDefinition());
        return new DiscriminatorJdbcBuilder<T>(column, this);
    }

	private static class MapperKeyFactory implements UnaryFactoryWithException<ResultSet, MapperKey<JdbcColumnKey>, SQLException> {
		@Override
        public MapperKey<JdbcColumnKey> newInstance(ResultSet set) throws SQLException {
            return JdbcColumnKey.mapperKey(set.getMetaData());
        }
	}

	private class SetRowMapperFactory<T> implements UnaryFactory<MapperKey<JdbcColumnKey>, SetRowMapper<ResultSet,ResultSet,T,SQLException>> {
		private final ClassMeta<T> classMeta;

		public SetRowMapperFactory(ClassMeta<T> classMeta) {
			this.classMeta = classMeta;
		}

		@Override
        public SetRowMapper<ResultSet,ResultSet,T,SQLException> newInstance(MapperKey<JdbcColumnKey> jdbcColumnKeyMapperKey) {
            final JdbcMapperBuilder<T> builder = newBuilder(classMeta);

            for(JdbcColumnKey key : jdbcColumnKeyMapperKey.getColumns()) {
                builder.addMapping(key);
            }
            return builder.mapper();
        }
	}
}
