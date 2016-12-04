package org.simpleflatmapper.datastax;

import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.DriverException;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.datastax.impl.ResultSetEnumarable;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.AbstractMapperBuilder;
import org.simpleflatmapper.map.mapper.JoinMapper;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.map.mapper.StaticSetRowMapper;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.UnaryFactory;

/**
 * @see DatastaxMapperFactory
 * @param <T> the targeted type of the jdbcMapper
 */
public final class DatastaxMapperBuilder<T> extends AbstractMapperBuilder<Row, T, DatastaxColumnKey, DatastaxMapper<T>, DatastaxMapperBuilder<T>> {

    public static final KeyFactory<DatastaxColumnKey> KEY_FACTORY = new KeyFactory<DatastaxColumnKey>() {
        @Override
        public DatastaxColumnKey newKey(String name, int i) {
            return new DatastaxColumnKey(name, i);
        }
    };

    /**
     * @param classMeta                  the meta for the target class.
     * @param mapperConfig               the mapperConfig.
     * @param getterFactory              the Getter factory.
     * @param parentBuilder              the parent builder, null if none.
     */
    public DatastaxMapperBuilder(
            final ClassMeta<T> classMeta,
            MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> mapperConfig,
            GetterFactory<GettableByIndexData, DatastaxColumnKey> getterFactory,
            MappingContextFactoryBuilder<GettableByIndexData, DatastaxColumnKey> parentBuilder) {
        super(classMeta, parentBuilder, mapperConfig, new MapperSourceImpl<GettableByIndexData, DatastaxColumnKey>(GettableByIndexData.class, getterFactory), KEY_FACTORY, 0);
    }


    /**
     * add a new mapping to the specified property with the specified index,  the specified type.
     *
     * @param column           the property name
     * @param index            the property index
     * @param dataType          the property type, @see java.sql.Types
     * @param properties the property properties
     * @return the current builder
     */
    public DatastaxMapperBuilder<T> addMapping(final String column, final int index, final DataType dataType, Object... properties) {
        return addMapping(new DatastaxColumnKey(column, index, dataType), properties);
    }

    /**
     * add the all the property present in the metaData
     *
     * @param metaData the metaDAta
     * @return the current builder
     */
    public DatastaxMapperBuilder<T> addMapping(final ColumnDefinitions metaData) {
        for (int i = 1; i <= metaData.size(); i++) {
            addMapping(metaData.getName(i), i, metaData.getType(i), new Object[0]);
        }

        return this;
    }

    @Override
    protected DatastaxMapper<T> newJoinMapper(Mapper<Row, T> mapper) {
        return new JoinDatastaxMapper<T>(mapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }

    private static class JoinDatastaxMapper<T> extends JoinMapper<Row, ResultSet, T, DriverException> implements DatastaxMapper<T> {
        public JoinDatastaxMapper(Mapper<Row, T> mapper, ConsumerErrorHandler errorHandler, MappingContextFactory<? super Row> mappingContextFactory) {
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
    protected DatastaxMapper<T> newStaticMapper(Mapper<Row, T> mapper) {
        return new StaticDatastaxMapper<T>(mapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }

    public static class StaticDatastaxMapper<T> extends StaticSetRowMapper<Row, ResultSet, T, DriverException> implements DatastaxMapper<T> {

        public StaticDatastaxMapper(Mapper<Row, T> mapper, ConsumerErrorHandler errorHandler, MappingContextFactory<? super Row> mappingContextFactory) {
            super(mapper, errorHandler, mappingContextFactory, new ResultSetEnumarableFactory());
        }
    }
}