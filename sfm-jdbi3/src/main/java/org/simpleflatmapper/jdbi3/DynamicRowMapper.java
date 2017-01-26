package org.simpleflatmapper.jdbi3;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.simpleflatmapper.jdbc.DynamicJdbcMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DynamicRowMapper<T> implements RowMapper<T> {

    private final DynamicJdbcMapper<T> dynamicMapper;

    public DynamicRowMapper(DynamicJdbcMapper<T> dynamicMapper) {
        this.dynamicMapper = dynamicMapper;
    }

    @Override
    public T map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return dynamicMapper.map(resultSet);
    }

    @Override
    public RowMapper<T> specialize(ResultSet rs, StatementContext ctx) throws SQLException {
        return new StaticRowMapper<T>(dynamicMapper.getMapper(rs.getMetaData()));
    }
}
