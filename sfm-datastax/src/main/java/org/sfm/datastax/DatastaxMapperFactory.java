package org.sfm.datastax;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import org.sfm.datastax.impl.DatastaxMappingContextFactoryBuilder;
import org.sfm.datastax.impl.RowGetterFactory;
import org.sfm.map.*;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.AbstractMapperFactory;
import org.sfm.map.mapper.DynamicSetRowMapper;
import org.sfm.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.sfm.map.mapper.MapperKey;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.UnaryFactory;
import org.sfm.utils.UnaryFactoryWithException;

import java.lang.reflect.Type;

public class DatastaxMapperFactory extends AbstractMapperFactory<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey, Row>, DatastaxMapperFactory> {

    private GetterFactory<GettableByIndexData, DatastaxColumnKey> getterFactory = new RowGetterFactory(this);

    private DatastaxMapperFactory() {
        super(new FieldMapperColumnDefinitionProviderImpl<DatastaxColumnKey, Row>(), FieldMapperColumnDefinition.<DatastaxColumnKey, Row>identity());
    }

    public static DatastaxMapperFactory newInstance() {
        return new DatastaxMapperFactory();
    }


    public <T> DatastaxMapperBuilder<T> newBuilder(Class<T> type) {
        return newBuilder((Type)type);
    }

    public <T> DatastaxMapperBuilder<T> newBuilder(TypeReference<T> type) {
        return newBuilder(type.getType());
    }

    public <T> DatastaxMapperBuilder<T> newBuilder(Type type) {
        final ClassMeta<T> classMeta = getClassMeta(type);
        return newBuilder(classMeta);
    }

    public <T> DatastaxMapperBuilder<T> newBuilder(ClassMeta<T> classMeta) {
        return new DatastaxMapperBuilder<T>(classMeta,
                mapperConfig(),
                getterFactory,
                new DatastaxMappingContextFactoryBuilder());
    }

    public <T> DatastaxMapper<T> mapTo(Class<T> type) {
        return mapTo((Type) type);
    }
    public <T> DatastaxMapper<T> mapTo(TypeReference<T> type) {
        return mapTo(type.getType());
    }
    public <T> DatastaxMapper<T> mapTo(Type type) {
        final ClassMeta<T> classMeta = getClassMeta(type);
        return new DynamicDatastaxSetRowMapper<T>(new MapperFactory<T>(classMeta), new MapperKeyFromRowFactory(), new MapperKeyFromSet());
    }

    private static class MapperKeyFromRowFactory implements UnaryFactoryWithException<Row, MapperKey<DatastaxColumnKey>, DriverException> {
        @Override
        public MapperKey<DatastaxColumnKey> newInstance(Row row) throws DriverException {
            return DatastaxColumnKey.mapperKey(row.getColumnDefinitions());
        }
    }

    private static class MapperKeyFromSet implements UnaryFactoryWithException<ResultSet, MapperKey<DatastaxColumnKey>, DriverException> {
        @Override
        public MapperKey<DatastaxColumnKey> newInstance(ResultSet rows) throws DriverException {
            return DatastaxColumnKey.mapperKey(rows.getColumnDefinitions());
        }
    }

    private class MapperFactory<T> implements UnaryFactory<MapperKey<DatastaxColumnKey>, SetRowMapper<Row, ResultSet, T, DriverException>> {
        private final ClassMeta<T> classMeta;

        public MapperFactory(ClassMeta<T> classMeta) {
            this.classMeta = classMeta;
        }

        @Override
        public SetRowMapper<Row, ResultSet, T, DriverException> newInstance(MapperKey<DatastaxColumnKey> datastaxColumnKeyMapperKey) {
            DatastaxMapperBuilder<T> builder = newBuilder(classMeta);

            for(DatastaxColumnKey key : datastaxColumnKeyMapperKey.getColumns()) {
                builder.addMapping(key);
            }

            return builder.mapper();
        }
    }

    private static class DynamicDatastaxSetRowMapper<T>
            extends DynamicSetRowMapper<Row, ResultSet, T, DriverException, DatastaxColumnKey>
            implements DatastaxMapper<T> {

        public DynamicDatastaxSetRowMapper(
                UnaryFactory<MapperKey<DatastaxColumnKey>, SetRowMapper<Row, ResultSet, T, DriverException>> mapperFactory,
                UnaryFactoryWithException<Row, MapperKey<DatastaxColumnKey>, DriverException> mapperKeyFromRow,
                UnaryFactoryWithException<ResultSet, MapperKey<DatastaxColumnKey>, DriverException> mapperKeyFromSet) {
            super(mapperFactory, mapperKeyFromRow, mapperKeyFromSet);        }
    }
}
