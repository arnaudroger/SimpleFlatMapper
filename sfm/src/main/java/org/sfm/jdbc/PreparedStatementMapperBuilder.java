package org.sfm.jdbc;


import org.sfm.jdbc.impl.PreparedStatementSetterFactory;
import org.sfm.map.*;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ConstantTargetFieldMapperFactorImpl;
import org.sfm.map.impl.fieldmapper.ConstantTargetFieldMapperFactory;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

import java.sql.PreparedStatement;

public class PreparedStatementMapperBuilder<T> extends AbstractWriterBuilder<PreparedStatement, T, JdbcColumnKey, PreparedStatementMapperBuilder<T>> {

    public PreparedStatementMapperBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> mapperConfig,
            ConstantTargetFieldMapperFactory<PreparedStatement, JdbcColumnKey> preparedStatementFieldMapperFactory) {
        super(classMeta, PreparedStatement.class, mapperConfig, preparedStatementFieldMapperFactory);
    }

    public static <T> PreparedStatementMapperBuilder<T> newBuilder(Class<T> clazz) {
        ClassMeta<T> classMeta = ReflectionService.newInstance().getClassMeta(clazz);
        return PreparedStatementMapperBuilder.newBuilder(classMeta);
    }

    public static <T> PreparedStatementMapperBuilder<T> newBuilder(ClassMeta<T> classMeta) {
        MapperConfig<JdbcColumnKey,FieldMapperColumnDefinition<JdbcColumnKey>> config =
                MapperConfig.<JdbcColumnKey>fieldMapperConfig();
        PreparedStatementMapperBuilder<T> builder =
                new PreparedStatementMapperBuilder<T>(
                        classMeta,
                        config,
                        ConstantTargetFieldMapperFactorImpl.instance(new PreparedStatementSetterFactory()));
        return builder;
    }

    @Override
    protected Instantiator<T, PreparedStatement> getInstantiator() {
        return new NullInstantiator<T>();
    }

    @Override
    protected JdbcColumnKey newKey(String column, int i) {
        return new JdbcColumnKey(column, i);
    }

    private static class NullInstantiator<T> implements Instantiator<T, PreparedStatement> {
        @Override
        public PreparedStatement newInstance(T o) throws Exception {
            throw new UnsupportedOperationException();
        }
    }

    protected int getStartingIndex() {
        return 1;
    }


}
