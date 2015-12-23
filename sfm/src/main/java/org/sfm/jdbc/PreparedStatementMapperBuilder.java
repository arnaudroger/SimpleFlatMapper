package org.sfm.jdbc;


import org.sfm.jdbc.named.NamedSqlQuery;
import org.sfm.map.*;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ConstantTargetFieldMapperFactory;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.meta.ClassMeta;

import java.sql.PreparedStatement;

public class PreparedStatementMapperBuilder<T> extends AbstractWriterBuilder<PreparedStatement, T, JdbcColumnKey, PreparedStatementMapperBuilder<T>> {

    public PreparedStatementMapperBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> mapperConfig,
            ConstantTargetFieldMapperFactory<PreparedStatement, JdbcColumnKey> preparedStatementFieldMapperFactory) {
        super(classMeta, PreparedStatement.class, mapperConfig, preparedStatementFieldMapperFactory);
    }

    @Override
    protected Instantiator<T, PreparedStatement> getInstantiator() {
        return new NullInstantiator<T>();
    }

    @Override
    protected JdbcColumnKey newKey(String column, int i, FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition) {
        JdbcColumnKey key = new JdbcColumnKey(column, i);

        SqlTypeColumnProperty typeColumnProperty = columnDefinition.lookFor(SqlTypeColumnProperty.class);

        if (typeColumnProperty == null) {
            FieldMapperColumnDefinition<JdbcColumnKey> globalDef = mapperConfig.columnDefinitions().getColumnDefinition(key);
            typeColumnProperty = globalDef.lookFor(SqlTypeColumnProperty.class);
        }

        if (typeColumnProperty != null) {
            return new JdbcColumnKey(key.getName(), key.getIndex(), typeColumnProperty.getSqlType(), key);
        }

        return key;
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

    public PreparedStatementMapper<T> to(NamedSqlQuery query) {
        for(int i = 0; i < query.getParametersSize(); i++) {
            addColumn(query.getParameter(i).getName());
        }
        Mapper<T, PreparedStatement> mapper = mapper();

        return new PreparedStatementMapper<T>(query, mapper);
    }
}
