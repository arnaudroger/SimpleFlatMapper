package org.sfm.jdbc;

import org.sfm.jdbc.impl.*;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.*;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.impl.*;
import org.sfm.map.impl.context.MappingContextFactoryBuilder;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.Enumarable;
import org.sfm.utils.OneArgumentFactory;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @param <T> the targeted type of the jdbcMapper
 */
public final class JdbcMapperBuilder<T> extends AbstractMapperBuilder<ResultSet, T, JdbcColumnKey, JdbcMapper<T>, JdbcMapperBuilder<T>>{

    public static final MapperSourceImpl<ResultSet, JdbcColumnKey> FIELD_MAPPER_SOURCE =
            new MapperSourceImpl<ResultSet, JdbcColumnKey>(ResultSet.class, new ResultSetGetterFactory());


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
                MapperConfig.<ResultSet, JdbcColumnKey>fieldMapperConfig(),
                new ResultSetGetterFactory(),
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
             MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> mapperConfig,
             GetterFactory<ResultSet, JdbcColumnKey> getterFactory,
             MappingContextFactoryBuilder<ResultSet, JdbcColumnKey> parentBuilder) {
        super(classMeta, parentBuilder, mapperConfig, FIELD_MAPPER_SOURCE.getterFactory(getterFactory), 1);
    }


    /**
     * add a new mapping to the specified column with the specified index and the specified type.
     *
     * @param column  the column name
     * @param index   the column index
     * @param sqlType the column type, @see java.sql.Types
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final int index, final int sqlType) {
        addMapping(column, index, sqlType, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>identity());
        return this;
    }

    /**
     * add a new mapping to the specified column with the specified index,  the specified type.
     *
     * @param column           the column name
     * @param index            the column index
     * @param sqlType          the column type, @see java.sql.Types
     * @param columnDefinition the column definition
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final int index, final int sqlType, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
        return addMapping(new JdbcColumnKey(column, index, sqlType), columnDefinition);
    }

    /**
     * add a new mapping to the specified column with the specified index,  the specified type.
     *
     * @param column           the column name
     * @param index            the column index
     * @param sqlType          the column type, @see java.sql.Types
     * @param properties the column properties
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final int index, final int sqlType, ColumnProperty... properties) {
        return addMapping(new JdbcColumnKey(column, index, sqlType), properties);
    }

    /**
     * add the all the column present in the metaData
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
    protected JdbcColumnKey key(String column, int index) {
        return new JdbcColumnKey(column, index);
    }

    @Override
    protected JdbcMapper<T> newJoinJdbcMapper(Mapper<ResultSet, T> mapper) {
        return new JoinDatastaxMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }

    private static class JoinDatastaxMapper<T> extends  JoinMapperImpl<ResultSet, ResultSet, T, SQLException> implements JdbcMapper<T> {
        public JoinDatastaxMapper(Mapper<ResultSet, T> mapper, RowHandlerErrorHandler errorHandler, MappingContextFactory<? super ResultSet> mappingContextFactory) {
            super(mapper, errorHandler, mappingContextFactory, new ResultSetEnumarableFactory());
        }

        @Override
        public MappingContext<? super ResultSet> newMappingContext(ResultSet rs) {
            return newMappingContext();
        }
    }

    private static class ResultSetEnumarableFactory implements OneArgumentFactory<ResultSet, Enumarable<ResultSet>> {
        @Override
        public Enumarable<ResultSet> newInstance(ResultSet rows) {
            return new ResultSetEnumarable(rows);
        }
    }
    @Override
    protected JdbcMapper<T> newStaticJdbcMapper(Mapper<ResultSet, T> mapper) {
        return new StaticJdbcMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }
}