package org.simpleflatmapper.jdbi;

import org.simpleflatmapper.map.SourceMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultSfmResultSetMapper<T> implements ResultSetMapper<T> {

    private final SourceMapper<ResultSet, T> mapper;

    public DefaultSfmResultSetMapper(SourceMapper<ResultSet, T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public T map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return mapper.map(resultSet);
    }
}
