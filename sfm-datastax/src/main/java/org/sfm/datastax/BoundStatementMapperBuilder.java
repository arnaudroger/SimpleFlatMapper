package org.sfm.datastax;


import com.datastax.driver.core.BoundStatement;
import org.sfm.datastax.impl.BoundStatementSetterFactory;
import org.sfm.map.*;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ConstantTargetFieldMapperFactorImpl;
import org.sfm.map.impl.fieldmapper.ConstantTargetFieldMapperFactory;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

public class BoundStatementMapperBuilder<T> extends AbstractWriterBuilder<BoundStatement, T, DatastaxColumnKey, BoundStatementMapperBuilder<T>> {

    public BoundStatementMapperBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> mapperConfig,
            ConstantTargetFieldMapperFactory<BoundStatement, DatastaxColumnKey> BoundStatementFieldMapperFactory) {
        super(classMeta, BoundStatement.class, mapperConfig, BoundStatementFieldMapperFactory);
    }

    public static <T> BoundStatementMapperBuilder<T> newBuilder(Class<T> clazz) {
        ClassMeta<T> classMeta = ReflectionService.newInstance().getClassMeta(clazz);
        return BoundStatementMapperBuilder.newBuilder(classMeta);
    }

    public static <T> BoundStatementMapperBuilder<T> newBuilder(ClassMeta<T> classMeta) {
        MapperConfig<DatastaxColumnKey,FieldMapperColumnDefinition<DatastaxColumnKey>> config =
                MapperConfig.<T, DatastaxColumnKey>fieldMapperConfig();
        BoundStatementMapperBuilder<T> builder =
                new BoundStatementMapperBuilder<T>(
                        classMeta,
                        config,
                        ConstantTargetFieldMapperFactorImpl.instance(new BoundStatementSetterFactory()));
        return builder;
    }

    @Override
    protected Instantiator<T, BoundStatement> getInstantiator() {
        return new NullInstantiator<T>();
    }

    @Override
    protected DatastaxColumnKey newKey(String column, int i) {
        return new DatastaxColumnKey(column, i);
    }

    private static class NullInstantiator<T> implements Instantiator<T, BoundStatement> {
        @Override
        public BoundStatement newInstance(T o) throws Exception {
            throw new UnsupportedOperationException();
        }
    }

    protected int getStartingIndex() {
        return 0;
    }


}
