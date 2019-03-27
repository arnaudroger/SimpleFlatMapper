package org.simpleflatmapper.datastax;

import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.DriverException;
import org.simpleflatmapper.datastax.impl.DatastaxKeySourceGetter;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.mapper.DefaultSetRowMapperBuilder;
import org.simpleflatmapper.map.mapper.MapperBuilder;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.datastax.impl.ResultSetEnumerable;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.UnaryFactory;

import java.util.Iterator;
import java.util.List;

//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

/**
 * @see DatastaxMapperFactory
 * @param <T> the targeted type of the jdbcMapper
 */
public final class DatastaxMapperBuilder<T> extends MapperBuilder<Row, ResultSet, T, DatastaxColumnKey, DriverException, SetRowMapper<Row, ResultSet, T, DriverException>, DatastaxMapper<T>, DatastaxMapperBuilder<T>> {

    public static final KeyFactory<DatastaxColumnKey> KEY_FACTORY = new KeyFactory<DatastaxColumnKey>() {
        @Override
        public DatastaxColumnKey newKey(String name, int i) {
            return new DatastaxColumnKey(name, i);
        }
    };
    public static final Function<Object[], ColumnDefinition<DatastaxColumnKey, ?>> COLUMN_DEFINITION_FACTORY = FieldMapperColumnDefinition.factory();


    /**
     * @param classMeta                  the meta for the target class.
     * @param mapperConfig               the mapperConfig.
     * @param getterFactory              the Getter factory.
     * @param parentBuilder              the parent builder, null if none.
     */
    public DatastaxMapperBuilder(
            final ClassMeta<T> classMeta,
            MapperConfig<DatastaxColumnKey, Row> mapperConfig,
            GetterFactory<? super GettableByIndexData, DatastaxColumnKey> getterFactory,
            MappingContextFactoryBuilder<Row, DatastaxColumnKey> parentBuilder) {
        this(classMeta, mapperConfig, new ContextualGetterFactoryAdapter<GettableByIndexData, DatastaxColumnKey>(getterFactory), parentBuilder);
    }

    /**
     * @param classMeta                  the meta for the target class.
     * @param mapperConfig               the mapperConfig.
     * @param getterFactory              the Getter factory.
     * @param parentBuilder              the parent builder, null if none.
     */
    public DatastaxMapperBuilder(
            final ClassMeta<T> classMeta,
            MapperConfig<DatastaxColumnKey, Row> mapperConfig,
            ContextualGetterFactory<? super GettableByIndexData, DatastaxColumnKey> getterFactory,
            MappingContextFactoryBuilder<Row, DatastaxColumnKey> parentBuilder) {
        super(KEY_FACTORY, 
                new DefaultSetRowMapperBuilder<Row, ResultSet, T, DatastaxColumnKey, DriverException>(
                        classMeta, parentBuilder, mapperConfig,
                        new MapperSourceImpl<GettableByIndexData, DatastaxColumnKey>(GettableByIndexData.class, getterFactory),
                        KEY_FACTORY, new ResultSetEnumerableFactory(), DatastaxKeySourceGetter.INSTANCE),
                new BiFunction<SetRowMapper<Row, ResultSet, T, DriverException>, List<DatastaxColumnKey>, DatastaxMapper<T>>() {
                    @Override
                    public DatastaxMapper<T> apply(SetRowMapper<Row, ResultSet, T, DriverException> setRowMapper, List<DatastaxColumnKey> keys) {
                        return new DatastaxMapperImpl<T>(setRowMapper);
                    }
                },
                COLUMN_DEFINITION_FACTORY, 0);
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

    private static class ResultSetEnumerableFactory implements UnaryFactory<ResultSet, Enumerable<Row>> {
        @Override
        public Enumerable<Row> newInstance(ResultSet rows) {
            return new ResultSetEnumerable(rows);
        }
    }
    
    
    private static class DatastaxMapperImpl<T> implements DatastaxMapper<T> {
        private final SetRowMapper<Row, ResultSet, T, DriverException> setRowMapper;

        private DatastaxMapperImpl(SetRowMapper<Row, ResultSet, T, DriverException> setRowMapper) {
            this.setRowMapper = setRowMapper;
        }
        @Override
        public T map(Row source) throws MappingException {
            return setRowMapper.map(source);
        }

        @Override
        public T map(Row source, MappingContext<? super Row> context) throws MappingException {
            return setRowMapper.map(source, context);
        }

        @Override
        public <H extends CheckedConsumer<? super T>> H forEach(ResultSet source, H handler) throws DriverException, MappingException {
            return setRowMapper.forEach(source, handler);
        }

        @Override
        public Iterator<T> iterator(ResultSet source) throws DriverException, MappingException {
            return setRowMapper.iterator(source);
        }

        @Override
        public Enumerable<T> enumerate(ResultSet source) throws DriverException, MappingException {
            return setRowMapper.enumerate(source);
        }

        //IFJAVA8_START
        @Override
        public Stream<T> stream(ResultSet source) throws DriverException, MappingException {
            return setRowMapper.stream(source);
        }

        //IFJAVA8_END

    }
}