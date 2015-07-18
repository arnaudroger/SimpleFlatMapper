package org.sfm.datastax;

import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.DriverException;
import org.sfm.datastax.impl.DatastaxMappingContextFactoryBuilder;
import org.sfm.datastax.impl.ResultSetEnumarable;
import org.sfm.datastax.impl.RowGetterFactory;
import org.sfm.map.*;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.impl.*;
import org.sfm.map.impl.context.MappingContextFactoryBuilder;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.Enumarable;
import org.sfm.utils.UnaryFactory;

import java.lang.reflect.Type;
import java.sql.SQLException;

/**
 * @see DatastaxMapperFactory
 * @param <T> the targeted type of the jdbcMapper
 */
public final class DatastaxMapperBuilder<T> extends AbstractMapperBuilder<Row, T, DatastaxColumnKey, DatastaxMapper<T>, DatastaxMapperBuilder<T>> {

    public static final MapperSourceImpl<GettableData, DatastaxColumnKey> FIELD_MAPPER_SOURCE =
            new MapperSourceImpl<GettableData, DatastaxColumnKey>(GettableData.class, new RowGetterFactory());

    /**
     * @param classMeta                  the meta for the target class.
     * @param mapperConfig               the mapperConfig.
     * @param getterFactory              the Getter factory.
     * @param parentBuilder              the parent builder, null if none.
     */
    public DatastaxMapperBuilder(
            final ClassMeta<T> classMeta,
            MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey, Row>> mapperConfig,
            GetterFactory<GettableData, DatastaxColumnKey> getterFactory,
            MappingContextFactoryBuilder<Row, DatastaxColumnKey> parentBuilder) {
        super(classMeta, parentBuilder, mapperConfig, FIELD_MAPPER_SOURCE.getterFactory(getterFactory), 0);
    }


    /**
     * add a new mapping to the specified column with the specified index,  the specified type.
     *
     * @param column           the column name
     * @param index            the column index
     * @param dataType          the column type, @see java.sql.Types
     * @param properties the column properties
     * @return the current builder
     */
    public DatastaxMapperBuilder<T> addMapping(final String column, final int index, final DataType dataType, ColumnProperty... properties) {
        return addMapping(new DatastaxColumnKey(column, index, dataType), properties);
    }

    /**
     * add the all the column present in the metaData
     *
     * @param metaData the metaDAta
     * @return the current builder
     * @throws SQLException when an error occurs getting the metaData
     */
    public DatastaxMapperBuilder<T> addMapping(final ColumnDefinitions metaData) throws SQLException {
        for (int i = 1; i <= metaData.size(); i++) {
            addMapping(metaData.getName(i), i, metaData.getType(i));
        }

        return this;
    }

    @Override
    protected DatastaxColumnKey key(String column, int index) {
        return new DatastaxColumnKey(column, index);
    }

    @Override
    protected DatastaxMapper<T> newJoinJdbcMapper(Mapper<Row, T> mapper) {
        return new JoinDatastaxMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }

    private static class JoinDatastaxMapper<T> extends  JoinMapperImpl<Row, ResultSet, T, DriverException> implements DatastaxMapper<T> {
        public JoinDatastaxMapper(Mapper<Row, T> mapper, RowHandlerErrorHandler errorHandler, MappingContextFactory<? super Row> mappingContextFactory) {
            super(mapper, errorHandler, mappingContextFactory, new ResultSetEnumarableFactory());
        }
    }

    private static class ResultSetEnumarableFactory implements UnaryFactory<ResultSet, Enumarable<Row>> {
        @Override
        public Enumarable<Row> newInstance(ResultSet rows) {
            return new ResultSetEnumarable(rows);
        }
    }

    @Override
    protected DatastaxMapper<T> newStaticJdbcMapper(Mapper<Row, T> mapper) {
        return new StaticDatastaxMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }

    public static class StaticDatastaxMapper<T> extends StaticSetRowMapper<Row, ResultSet, T, DriverException> implements DatastaxMapper<T> {

        public StaticDatastaxMapper(Mapper<Row, T> mapper, RowHandlerErrorHandler errorHandler, MappingContextFactory<? super Row> mappingContextFactory) {
            super(mapper, errorHandler, mappingContextFactory, new ResultSetEnumarableFactory());
        }
    }
}