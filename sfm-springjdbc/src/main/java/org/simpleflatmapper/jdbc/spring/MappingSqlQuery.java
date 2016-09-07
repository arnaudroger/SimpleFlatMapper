package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.named.NamedParameter;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
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
        setParameters(extractParameters(sql));
        statementCallback = new PreparedStatementCallbackImpl<T>(mapper);

        afterPropertiesSet();
    }

    private SqlParameter[] extractParameters(String sql) {
        NamedSqlQuery query = NamedSqlQuery.parse(sql);

        SqlParameter[] params = new SqlParameter[query.getParametersSize()];

        for(int i = 0; i < params.length; i++) {
            NamedParameter namedParameter = query.getParameter(i);
            params[i] = new SqlParameter(namedParameter.getName(), 0);
        }

        return params;
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
