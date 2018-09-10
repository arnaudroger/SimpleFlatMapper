package org.simpleflatmapper.jdbi;

import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DynamicSfmResultSetMapper<T> implements ResultSetMapper<T> {

    private static final String STATEMENT_MAPPER = DynamicSfmResultSetMapper.class.getName() + ".dynamicMapper";
    private final DynamicJdbcMapper<T> dynamicMapper;

    public DynamicSfmResultSetMapper(DynamicJdbcMapper<T> dynamicMapper) {
        this.dynamicMapper = dynamicMapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        JdbcMapper<T> mapper = (JdbcMapper<T>) statementContext.getAttribute(STATEMENT_MAPPER);

        if (mapper == null) {
            mapper = dynamicMapper.getMapper(resultSet.getMetaData());
            statementContext.setAttribute(STATEMENT_MAPPER, mapper);
        }

        return mapper.map(resultSet, mapper.newMappingContext(resultSet));
    }
}
