package org.sfm.datastax;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Statement;
import org.sfm.datastax.impl.SettableDataSetterFactory;
import org.sfm.map.MapperConfig;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ConstantTargetFieldMapperFactorImpl;
import org.sfm.map.mapper.MapperCache;
import org.sfm.map.mapper.MapperKey;
import org.sfm.reflect.meta.ClassMeta;

public class DatastaxBinder<T> {
    private final MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> config;
    private final ClassMeta<T> classMeta;

    private final MapperCache<DatastaxColumnKey, BoundStatementMapper<T>> cache = new MapperCache<DatastaxColumnKey, BoundStatementMapper<T>>();

    public DatastaxBinder(ClassMeta<T> classMeta, MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> config) {
        this.classMeta = classMeta;
        this.config = config;
    }

    public BoundStatementMapper<T> mapTo(PreparedStatement statement) {
        return mapTo(statement.getVariables());
    }

    public BoundStatementMapper<T> mapTo(ColumnDefinitions variables) {
        MapperKey<DatastaxColumnKey> mapperKey = DatastaxColumnKey.mapperKey(variables);
        BoundStatementMapper mapper = cache.get(mapperKey);

        if (mapper == null) {
            mapper = createMapper(mapperKey);
        }
        return mapper;
    }

    protected BoundStatementMapper<T> createMapper(MapperKey<DatastaxColumnKey> mapperKey) {
        BoundStatementMapper mapper;SettableDataMapperBuilder<T> settableDataMapperBuilder = new SettableDataMapperBuilder<T>(classMeta, config,
                ConstantTargetFieldMapperFactorImpl.instance(new SettableDataSetterFactory(config, classMeta.getReflectionService())));
        for(DatastaxColumnKey columnKey : mapperKey.getColumns()) {
            settableDataMapperBuilder.addColumn(columnKey);
        }
        mapper = new BoundStatementMapper<T>(settableDataMapperBuilder.mapper());
        cache.add(mapperKey, mapper);
        return mapper;
    }

    public Statement mapTo(T value, PreparedStatement preparedStatement) throws Exception {
        BoundStatementMapper<T> statementMapper = mapTo(preparedStatement);
        BoundStatement boundStatement = preparedStatement.bind();
        return statementMapper.mapTo(value, boundStatement);
    }
}
