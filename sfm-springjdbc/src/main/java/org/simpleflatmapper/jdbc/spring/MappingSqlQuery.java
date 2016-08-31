package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.object.SqlQuery;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class MappingSqlQuery<T> extends SqlQuery<T> {

    private final PreparedStatementCallbackImpl<T> statementCallback;

    public MappingSqlQuery(DataSource ds, String sql, JdbcMapper<T> mapper) {
        super(ds, sql);
        statementCallback = new PreparedStatementCallbackImpl<>(mapper);
    }

    public List<T> execute(Object[] params, Map<?, ?> context) throws DataAccessException {
        validateParameters(params);
        return query(newPreparedStatementCreator(params));
    }

    public List<T> executeByNamedParam(Map<String, ?> paramMap, Map<?, ?> context) throws DataAccessException {
        validateNamedParameters(paramMap);
        ParsedSql parsedSql = getParsedSql();
        MapSqlParameterSource paramSource = new MapSqlParameterSource(paramMap);
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, getDeclaredParameters());
        return query(newPreparedStatementCreator(sqlToUse, params));
    }

    private List<T> query(PreparedStatementCreator preparedStatementCreator) {
        return getJdbcTemplate().execute(preparedStatementCreator, statementCallback);
    }

    @Override
    protected RowMapper<T> newRowMapper(Object[] parameters, Map<?, ?> context) {
        throw  new UnsupportedOperationException();
    }
}
