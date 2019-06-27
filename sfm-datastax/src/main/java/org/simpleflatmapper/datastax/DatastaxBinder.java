package org.simpleflatmapper.datastax;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.SettableByIndexData;
import com.datastax.driver.core.Statement;
import org.simpleflatmapper.datastax.impl.DatastaxMapperKeyComparator;
import org.simpleflatmapper.datastax.impl.SettableDataSetterFactory;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactoryImpl;
import org.simpleflatmapper.map.mapper.MapperCache;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.reflect.meta.ArrayClassMeta;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.property.SpeculativeArrayIndexResolutionProperty;

public class DatastaxBinder<T> {
    private final MapperConfig<DatastaxColumnKey, ?> config;
    private final ClassMeta<T> classMeta;

    private final MapperCache<DatastaxColumnKey, BoundStatementMapper<T>> cache =
            new MapperCache<DatastaxColumnKey, BoundStatementMapper<T>>(DatastaxMapperKeyComparator.INSTANCE);

    public DatastaxBinder(ClassMeta<T> classMeta, MapperConfig<DatastaxColumnKey, ?> config) {
        this.classMeta = classMeta;
        this.config = config;
    }

    public BoundStatementMapper<T> mapTo(PreparedStatement statement) {
        return mapTo(statement.getVariables());
    }

    public BoundStatementMapper<T> mapTo(ColumnDefinitions variables) {
        MapperKey<DatastaxColumnKey> mapperKey = DatastaxColumnKey.mapperKey(variables);
        BoundStatementMapper<T> mapper = cache.get(mapperKey);

        if (mapper == null) {
            mapper = createMapper(mapperKey);
        }
        return mapper;
    }

    protected BoundStatementMapper<T> createMapper(MapperKey<DatastaxColumnKey> mapperKey) {
        BoundStatementMapper<T> mapper;
        SettableDataMapperBuilder<T> settableDataMapperBuilder = new SettableDataMapperBuilder<T>(classMeta, config,
                ConstantTargetFieldMapperFactoryImpl.newInstance(new SettableDataSetterFactory(config, classMeta.getReflectionService()), SettableByIndexData.class));
        Object[] properties;
        if (classMeta instanceof ArrayClassMeta) {
            properties = new Object[] {SpeculativeArrayIndexResolutionProperty.INSTANCE};
        } else {
            properties = new Object[0];
        }
        for(DatastaxColumnKey columnKey : mapperKey.getColumns()) {
            settableDataMapperBuilder.addColumn(columnKey, properties);
        }
        mapper = new BoundStatementMapper<T>(settableDataMapperBuilder.mapper());
        cache.add(mapperKey, mapper);
        return mapper;
    }

    public Statement mapTo(T value, PreparedStatement preparedStatement) {
        BoundStatementMapper<T> statementMapper = mapTo(preparedStatement);
        BoundStatement boundStatement = preparedStatement.bind();
        return statementMapper.mapTo(value, boundStatement);
    }
}
