package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.AbstractMapperBuilder;
import org.simpleflatmapper.map.mapper.JoinMapper;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.map.mapper.StaticSetRowMapper;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.UnaryFactory;
import org.simpleflatmapper.jdbc.impl.ResultSetEnumarable;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @param <T> the targeted type of the jdbcMapper
 */
public final class JdbcMapperBuilder<T> extends AbstractMapperBuilder<ResultSet, T, JdbcColumnKey, JdbcMapper<T>, JdbcMapperBuilder<T>> {

    private static final MapperSourceImpl<ResultSet, JdbcColumnKey> FIELD_MAPPER_SOURCE =
            new MapperSourceImpl<ResultSet, JdbcColumnKey>(ResultSet.class,  ResultSetGetterFactory.INSTANCE);
    private static final KeyFactory<JdbcColumnKey> KEY_FACTORY = new KeyFactory<JdbcColumnKey>() {
        @Override
        public JdbcColumnKey newKey(String name, int i) {
            return new JdbcColumnKey(name, i);
        }
    };


    /**
     * Build a new JdbcMapperBuilder targeting the type specified by the TypeReference. The TypeReference
     * allow you to provide a generic type with check of T<br>
     * <code>new TypeReference&lt;List&lt;String&gt;&gt;() {}</code>
     *
     * @param target the TypeReference to the type T to map to
     */
    public JdbcMapperBuilder(final TypeReference<T> target) {
        this(target.getType());
    }

    /**
     * Build a new JdbcMapperBuilder targeting the specified type.
     *
     * @param target the type
     */
    public JdbcMapperBuilder(final Type target) {
        this(target, ReflectionService.newInstance());
    }

    /**
     * Build a new JdbcMapperBuilder targeting the specified type with the specified ReflectionService.
     *
     * @param target         the type
     * @param reflectService the ReflectionService
     */
    public JdbcMapperBuilder(final Type target, ReflectionService reflectService) {
        this(reflectService.<T>getClassMeta(target),
                MapperConfig.<JdbcColumnKey>fieldMapperConfig(),
                ResultSetGetterFactory.INSTANCE,
                new JdbcMappingContextFactoryBuilder());
    }

    /**
     * @param classMeta                  the meta for the target class.
     * @param mapperConfig               the mapperConfig.
     * @param getterFactory              the Getter factory.
     * @param parentBuilder              the parent builder, null if none.
     */
    public JdbcMapperBuilder(
             final ClassMeta<T> classMeta,
             MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> mapperConfig,
             GetterFactory<ResultSet, JdbcColumnKey> getterFactory,
             MappingContextFactoryBuilder<ResultSet, JdbcColumnKey> parentBuilder) {
        super(classMeta,
                parentBuilder,
                mapperConfig,
                FIELD_MAPPER_SOURCE.getterFactory(getterFactory),
                KEY_FACTORY, 1);
    }


    /**
     * add a new mapping to the specified property with the specified index and the specified type.
     *
     * @param column  the property name
     * @param index   the property index
     * @param sqlType the property type, @see java.sql.Types
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final int index, final int sqlType) {
        addMapping(column, index, sqlType, FieldMapperColumnDefinition.<JdbcColumnKey>identity());
        return this;
    }

    /**
     * add a new mapping to the specified property with the specified index,  the specified type.
     *
     * @param column           the property name
     * @param index            the property index
     * @param sqlType          the property type, @see java.sql.Types
     * @param columnDefinition the property definition
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final int index, final int sqlType, FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition) {
        return addMapping(new JdbcColumnKey(column, index, sqlType), columnDefinition);
    }

    /**
     * add a new mapping to the specified property with the specified index,  the specified type.
     *
     * @param column           the property name
     * @param index            the property index
     * @param sqlType          the property type, @see java.sql.Types
     * @param properties the property properties
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final int index, final int sqlType, Object... properties) {
        return addMapping(new JdbcColumnKey(column, index, sqlType), properties);
    }

    /**
     * add the all the property present in the metaData
     *
     * @param metaData the metaDAta
     * @return the current builder
     * @throws SQLException when an error occurs getting the metaData
     */
    public JdbcMapperBuilder<T> addMapping(final ResultSetMetaData metaData) throws SQLException {
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            addMapping(metaData.getColumnLabel(i), i, metaData.getColumnType(i));
        }

        return this;
    }

    @Override
    protected JdbcMapper<T> newJoinMapper(Mapper<ResultSet, T> mapper) {
        return new JoinJdbcMapper<T>(mapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }

    private static class JoinJdbcMapper<T> extends JoinMapper<ResultSet, ResultSet, T, SQLException>
            implements JdbcMapper<T> {
        public JoinJdbcMapper(Mapper<ResultSet, T> mapper, ConsumerErrorHandler errorHandler, MappingContextFactory<? super ResultSet> mappingContextFactory) {
            super(mapper, errorHandler, mappingContextFactory, new ResultSetEnumarableFactory());
        }

        @Override
        public MappingContext<? super ResultSet> newMappingContext(ResultSet rs) {
            return getMappingContextFactory().newContext();
        }
    }

    private static class ResultSetEnumarableFactory implements UnaryFactory<ResultSet, Enumarable<ResultSet>> {
        @Override
        public Enumarable<ResultSet> newInstance(ResultSet rows) {
            return new ResultSetEnumarable(rows);
        }
    }
    @Override
    protected JdbcMapper<T> newStaticMapper(Mapper<ResultSet, T> mapper) {
        return new StaticJdbcSetRowMapper<T>(mapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }

    private static class StaticJdbcSetRowMapper<T> extends StaticSetRowMapper<ResultSet, ResultSet, T, SQLException> implements  JdbcMapper<T> {

        public StaticJdbcSetRowMapper(Mapper<ResultSet, T> mapper, ConsumerErrorHandler errorHandler, MappingContextFactory<? super ResultSet> mappingContextFactory) {
            super(mapper, errorHandler, mappingContextFactory, new ResultSetEnumarableFactory());
        }

        @Override
        public MappingContext<? super ResultSet> newMappingContext(ResultSet resultSet) throws SQLException {
            return getMappingContextFactory().newContext();
        }
    }
}