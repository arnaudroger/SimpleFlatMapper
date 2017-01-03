package org.simpleflatmapper.jdbi;

import org.simpleflatmapper.map.Mapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultSfmResultSetMapper<T> implements ResultSetMapper<T> {

    private final Mapper<ResultSet, T> mapper;

    public DefaultSfmResultSetMapper(Mapper<ResultSet, T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public T map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return mapper.map(resultSet);
    }
}
