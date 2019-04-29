package org.simpleflatmapper.datastax;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.SettableByIndexData;
import com.datastax.driver.core.exceptions.DriverException;
import org.simpleflatmapper.datastax.impl.DatastaxMapperKeyComparator;
import org.simpleflatmapper.datastax.impl.DatastaxMappingContextFactoryBuilder;
import org.simpleflatmapper.datastax.impl.RowGetterFactory;
import org.simpleflatmapper.datastax.impl.SettableDataSetterFactory;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactoryImpl;
import org.simpleflatmapper.map.mapper.DynamicSetRowMapper;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.UnaryFactory;
import org.simpleflatmapper.util.UnaryFactoryWithException;

import java.lang.reflect.Type;

public class DatastaxMapperFactory extends AbstractMapperFactory<DatastaxColumnKey, DatastaxMapperFactory, Row> {

    private DatastaxMapperFactory() {
        super(new FieldMapperColumnDefinitionProviderImpl<DatastaxColumnKey>(), FieldMapperColumnDefinition.<DatastaxColumnKey>identity(),
                new Function<DatastaxMapperFactory, ContextualGetterFactory<GettableByIndexData, DatastaxColumnKey>>() {
            @Override
            public ContextualGetterFactory<GettableByIndexData, DatastaxColumnKey> apply(DatastaxMapperFactory datastaxMapperFactory) {
                return new ContextualGetterFactoryAdapter<GettableByIndexData, DatastaxColumnKey>(new RowGetterFactory(datastaxMapperFactory));
            }
        });
    }

    private DatastaxMapperFactory(AbstractMapperFactory<DatastaxColumnKey, ?, Row> config) {
        super(config);
    }

    public static DatastaxMapperFactory newInstance() {
        return new DatastaxMapperFactory();
    }

    public static DatastaxMapperFactory newInstance(AbstractMapperFactory<DatastaxColumnKey, ?, Row> config) {
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
        MapperConfig<DatastaxColumnKey, Row> mapperConfig = mapperConfig(classMeta.getType());
        return new DatastaxMapperBuilder<T>(classMeta,
                mapperConfig,
                (ContextualGetterFactory<? super GettableByIndexData, DatastaxColumnKey>) getterFactory,
                new DatastaxMappingContextFactoryBuilder<Row>(!mapperConfig.unorderedJoin()));
    }

    public <T> SettableDataMapperBuilder<T> newBuilderFrom(TypeReference<T> type) {
        return newBuilderFrom(type.getType());
    }

    public <T> SettableDataMapperBuilder<T> newBuilderFrom(Class<T> type) {
        return newBuilderFrom((Type)type);
    }

    public <T> SettableDataMapperBuilder<T> newBuilderFrom(Type type) {
        ClassMeta<T> classMeta = getClassMeta(type);
        return newBuilderFrom(classMeta);
    }

    public <T> SettableDataMapperBuilder<T> newBuilderFrom(ClassMeta<T> classMeta) {
        MapperConfig<DatastaxColumnKey,Row> config = mapperConfig(classMeta.getType());
        return new SettableDataMapperBuilder<T>(
                classMeta,
                config,
                ConstantTargetFieldMapperFactoryImpl
                        .newInstance(new SettableDataSetterFactory(config, classMeta.getReflectionService()), SettableByIndexData.class));
    }

    public <T> DatastaxBinder<T> mapFrom(Class<T> type) {
        return mapFrom((Type) type);
    }
    public <T> DatastaxBinder<T> mapFrom(TypeReference<T> type) {
        return mapFrom(type.getType());
    }
    public <T> DatastaxBinder<T> mapFrom(Type type) {
        final ClassMeta<T> classMeta = getClassMeta(type);
        return new DatastaxBinder<T>(classMeta, mapperConfig(type));
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
            super(mapperFactory, mapperKeyFromRow, mapperKeyFromSet, DatastaxMapperKeyComparator.INSTANCE);        }
    }
}
