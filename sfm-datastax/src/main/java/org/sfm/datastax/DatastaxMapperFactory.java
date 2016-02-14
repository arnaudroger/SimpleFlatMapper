package org.sfm.datastax;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import org.sfm.datastax.impl.DatastaxColumnKeyComparator;
import org.sfm.datastax.impl.DatastaxMappingContextFactoryBuilder;
import org.sfm.datastax.impl.RowGetterFactory;
import org.sfm.datastax.impl.SettableDataSetterFactory;
import org.sfm.datastax.impl.mapping.DatastaxAliasProvider;
import org.sfm.map.*;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.AbstractMapperFactory;
import org.sfm.map.mapper.ConstantTargetFieldMapperFactorImpl;
import org.sfm.map.mapper.DynamicSetRowMapper;
import org.sfm.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.sfm.map.mapper.MapperKey;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.UnaryFactory;
import org.sfm.utils.UnaryFactoryWithException;

import java.lang.reflect.Type;

public class DatastaxMapperFactory extends AbstractMapperFactory<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>, DatastaxMapperFactory> {

    static {
        DatastaxAliasProvider.registers();
    }

    private GetterFactory<GettableByIndexData, DatastaxColumnKey> getterFactory = new RowGetterFactory(this);

    private DatastaxMapperFactory() {
        super(new FieldMapperColumnDefinitionProviderImpl<DatastaxColumnKey>(), FieldMapperColumnDefinition.<DatastaxColumnKey>identity());
    }

    private DatastaxMapperFactory(AbstractMapperFactory<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>, ?> config) {
        super(config);
    }

    public static DatastaxMapperFactory newInstance() {
        return new DatastaxMapperFactory();
    }

    public static DatastaxMapperFactory newInstance(AbstractMapperFactory<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>, ?> config) {
        return new DatastaxMapperFactory(config);
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

    public <T> SettableDataMapperBuilder<T> newBuilderFrom(Type type) {
        ClassMeta<T> classMeta = getClassMeta(type);
        MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> config = mapperConfig();
        return new SettableDataMapperBuilder<T>(
                classMeta,
                config,
                ConstantTargetFieldMapperFactorImpl
                        .instance(new SettableDataSetterFactory(config, classMeta.getReflectionService())));
    }

    public <T> DatastaxBinder<T> mapFrom(Class<T> type) {
        return mapFrom((Type) type);
    }
    public <T> DatastaxBinder<T> mapFrom(TypeReference<T> type) {
        return mapFrom(type.getType());
    }
    public <T> DatastaxBinder<T> mapFrom(Type type) {
        final ClassMeta<T> classMeta = getClassMeta(type);
        return new DatastaxBinder<T>(classMeta, mapperConfig());
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

    public <T, K> DatastaxCrudDSL<T, K> crud(Class<T> targetType, Class<K> keyType) {
        return crud((Type)targetType, (Type)keyType);
    }
    public <T, K> DatastaxCrudDSL<T, K> crud(Type targetType, Type keyType) {
        return new DatastaxCrudDSL<T, K>(targetType, keyType, this);
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
            super(mapperFactory, mapperKeyFromRow, mapperKeyFromSet, new DatastaxColumnKeyComparator());        }
    }
}
