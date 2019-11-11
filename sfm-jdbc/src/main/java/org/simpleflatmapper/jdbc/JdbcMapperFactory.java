package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.mapper.AbstractColumnNameDiscriminatorMapperFactory;
import org.simpleflatmapper.map.mapper.DynamicSourceFieldMapper;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.jdbc.impl.JdbcColumnKeyMapperKeyComparator;
import org.simpleflatmapper.jdbc.impl.PreparedStatementSetterFactory;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactoryImpl;
import org.simpleflatmapper.map.mapper.DynamicSetRowMapper;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.UnaryFactory;
import org.simpleflatmapper.util.UnaryFactoryWithException;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * JdbcMapperFactory allows you to customise the mappers and create an newInstance of it using a fluent syntax.
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
		extends AbstractColumnNameDiscriminatorMapperFactory<JdbcColumnKey, JdbcMapperFactory, ResultSet> {

    /**
	 * instantiate a new JdbcMapperFactory
	 * @return a new newInstance JdbcMapperFactory
	 */
	public static JdbcMapperFactory newInstance() {
		return new JdbcMapperFactory();
	}

	public static JdbcMapperFactory newInstance(
			AbstractMapperFactory<JdbcColumnKey, ?, ResultSet> config) {
		return new JdbcMapperFactory(config);
	}


	private JdbcMapperFactory(AbstractMapperFactory<JdbcColumnKey, ?, ResultSet> config) {
		super(config);
	}

	private JdbcMapperFactory() {
		super(new FieldMapperColumnDefinitionProviderImpl<JdbcColumnKey>(), FieldMapperColumnDefinition.<JdbcColumnKey>identity(), new ContextualGetterFactoryAdapter<ResultSet, JdbcColumnKey>(ResultSetGetterFactory.INSTANCE));
	}

	/**
	 * Override the default implementation of the GetterFactory used to get access to value from the ResultSet.
	 * @param getterFactory the getterFactory
	 * @return the current factory
	 */
	public JdbcMapperFactory getterFactory(final GetterFactory<ResultSet, JdbcColumnKey> getterFactory) {
		return addGetterFactory(new ContextualGetterFactoryAdapter<ResultSet, JdbcColumnKey>(getterFactory));
	}

	/**
	 * Override the default implementation of the GetterFactory used to get access to value from the ResultSet.
	 * @param getterFactory the getterFactory
	 * @return the current factory
	 */
	public JdbcMapperFactory addGetterFactory(final ContextualGetterFactory<ResultSet, JdbcColumnKey> getterFactory) {
		return super.addGetterFactory(getterFactory);
	}

	/**
	 * Associate the specified FieldMapper for the specified property.
	 * @param key the property
	 * @param fieldMapper the fieldMapper
	 * @return the current factory
	 */
	public JdbcMapperFactory addCustomFieldMapper(String key, FieldMapper<ResultSet, ?> fieldMapper) {
		return addColumnDefinition(key, FieldMapperColumnDefinition.<JdbcColumnKey>customFieldMapperDefinition(fieldMapper));
	}

	/**
	 * Associate the specified Getter for the specified property.
	 * @param key the property
	 * @param getter the getter
	 * @return the current factory
	 */
	public JdbcMapperFactory addCustomGetter(String key, Getter<ResultSet, ?> getter) {
		return addColumnDefinition(key, FieldMapperColumnDefinition.<JdbcColumnKey>customGetter(getter));
	}

	/**
	 * Will create a newInstance of JdbcMapper based on the specified metadata and the target class.
	 * @param target the target class of the jdbcMapper
	 * @param metaData the metadata to create the jdbcMapper from
     * @param <T> the jdbcMapper target type
	 * @return a jdbcMapper that will map the data represented by the metadata to an newInstance of target
     * @throws java.sql.SQLException if an error occurs getting the metaData
	 */
	public <T> JdbcMapper<T> newMapper(final Class<T> target, final ResultSetMetaData metaData) throws SQLException {
		JdbcMapperBuilder<T> builder = newBuilder(target);
		builder.addMapping(metaData);
		return builder.mapper();
	}
	
	/**
	 * Will create a newInstance of JdbcMapperBuilder on the specified target class.
	 * @param target the target class
     * @param <T> the jdbcMapper target type
	 * @return the builder
	 */
	public <T> JdbcMapperBuilder<T> newBuilder(final Class<T> target) {
		return newBuilder((Type)target);
	}

    /**
     * Will create a newInstance of JdbcMapperBuilder on the type T specified by the typeReference.
     * @param target the typeReference
     * @param <T> the jdbcMapper target type
     * @return the builder
     */
    public <T> JdbcMapperBuilder<T> newBuilder(final TypeReference<T> target) {
        return newBuilder(target.getType());
    }

    /**
     * Will create a newInstance of JdbcMapperBuilder on the specified type.
     * @param target the type
     * @param <T> the jdbcMapper target type
     * @return the builder
     */
    public <T> JdbcMapperBuilder<T> newBuilder(final Type target) {
		ClassMeta<T> classMeta = getClassMeta(target);
		return newBuilder(classMeta);
	}

	public <T> JdbcMapperBuilder<T> newBuilder(ClassMeta<T> classMeta) {
		MapperConfig<JdbcColumnKey, ResultSet> mapperConfig = mapperConfig(classMeta.getType());
		return new JdbcMapperBuilder<T>(
						classMeta,
						mapperConfig,
						getterFactory,
                        new JdbcMappingContextFactoryBuilder(!mapperConfig.unorderedJoin()));
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
		return new PreparedStatementMapperBuilder<T>(classMeta, mapperConfig(classMeta.getType()), ConstantTargetFieldMapperFactoryImpl.newInstance(PreparedStatementSetterFactory.INSTANCE, PreparedStatement.class));
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
	public <T> DynamicJdbcMapper<T> newMapper(final Class<T> target) {
		return newMapper((Type) target);
	}

    /**
     * Will create a DynamicMapper on the type specified by the TypeReference.
     * @param target the TypeReference
     * @param <T> the jdbcMapper target type
     * @return the DynamicMapper
     */
    public <T> DynamicJdbcMapper<T> newMapper(final TypeReference<T> target) {
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
	public <T> DynamicJdbcMapper<T> newMapper(final Type target) {
		final ClassMeta<T> classMeta = getClassMeta(target);
		return new DynamicJdbcSetRowMapper<T>(new SetRowMapperFactory<T>(classMeta),  new MapperKeyFactory(),  new MapperKeyFactory());
	}

	public <T> JdbcSourceFieldMapper<T> newSourceFieldMapper(Type target) {
		final ClassMeta<T> classMeta = getClassMeta(target);
		return new DynamicJdbSourceFieldMapper<T>(new SourceFieldMapperFactory<T>(classMeta),  new MapperKeyFactory());
	}
	public static class DynamicJdbSourceFieldMapper<T>
			extends DynamicSourceFieldMapper<ResultSet, T, JdbcColumnKey, SQLException>
			implements JdbcSourceFieldMapper<T> {

		public DynamicJdbSourceFieldMapper(
				UnaryFactory<MapperKey<JdbcColumnKey>, ContextualSourceFieldMapper<ResultSet, T>> mapperFactory,
				UnaryFactoryWithException<ResultSet, MapperKey<JdbcColumnKey>, SQLException> mapperKeyFromRow) {
			super(mapperFactory, mapperKeyFromRow, JdbcColumnKeyMapperKeyComparator.INSTANCE);
		}

		private ContextualSourceFieldMapper<ResultSet, T> getMapper(ResultSetMetaData metaData) throws SQLException {
			return getMapper(JdbcColumnKey.mapperKey(metaData));
		}

		@Override
		public String toString() {
			return "DynamicJdbcSetRowMapper{}";
		}

		@Override
		public MappingContext<? super ResultSet> newMappingContext(ResultSet resultSet) throws SQLException {
			JdbcSourceFieldMapper<T> mapper = (JdbcSourceFieldMapper<T>) getMapper(resultSet.getMetaData());
			return mapper.newMappingContext(resultSet);
		}
	}

	
	public static class DynamicJdbcSetRowMapper<T>
			extends DynamicSetRowMapper<ResultSet, ResultSet, T, SQLException, JdbcColumnKey>
			implements DynamicJdbcMapper<T> {

		public DynamicJdbcSetRowMapper(
				UnaryFactory<MapperKey<JdbcColumnKey>, SetRowMapper<ResultSet, ResultSet, T, SQLException>> mapperFactory,
				UnaryFactoryWithException<ResultSet, MapperKey<JdbcColumnKey>, SQLException> mapperKeyFromRow,
				UnaryFactoryWithException<ResultSet, MapperKey<JdbcColumnKey>, SQLException> mapperKeyFromSet) {
			super(mapperFactory, mapperKeyFromRow, mapperKeyFromSet, JdbcColumnKeyMapperKeyComparator.INSTANCE);
		}


		@Override
		public JdbcMapper<T> getMapper(ResultSetMetaData metaData) throws SQLException {
			return (JdbcMapper<T>) getMapper(JdbcColumnKey.mapperKey(metaData));
		}

		@Override
		public String toString() {
			return "DynamicJdbcSetRowMapper{}";
		}

		@Override
		public MappingContext<? super ResultSet> newMappingContext(ResultSet resultSet) throws SQLException {
			return getMapper(resultSet.getMetaData()).newMappingContext(resultSet);
		}
	}

	/**
     * Create a discriminator builder based on the specified property
     * @param column the discriminator property
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

	private class SourceFieldMapperFactory<T> implements UnaryFactory<MapperKey<JdbcColumnKey>, ContextualSourceFieldMapper<ResultSet, T>> {
		private final ClassMeta<T> classMeta;

		public SourceFieldMapperFactory(ClassMeta<T> classMeta) {
			this.classMeta = classMeta;
		}

		@Override
		public ContextualSourceFieldMapper<ResultSet, T> newInstance(MapperKey<JdbcColumnKey> jdbcColumnKeyMapperKey) {
			final JdbcMapperBuilder<T> builder = newBuilder(classMeta);

			for(JdbcColumnKey key : jdbcColumnKeyMapperKey.getColumns()) {
				builder.addMapping(key);
			}
			return builder.newSourceFieldMapper();
		}
	}
}
