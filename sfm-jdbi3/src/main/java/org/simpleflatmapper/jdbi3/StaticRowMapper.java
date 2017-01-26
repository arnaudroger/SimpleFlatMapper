package org.simpleflatmapper.jdbi3;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.simpleflatmapper.map.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StaticRowMapper<T> implements RowMapper<T> {

    private final Mapper<ResultSet, T> mapper;

    public StaticRowMapper(Mapper<ResultSet, T> mapper) {
        this.mapper = mapper;
    }


    @Override
    public T map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return mapper.map(resultSet);
    }
}
