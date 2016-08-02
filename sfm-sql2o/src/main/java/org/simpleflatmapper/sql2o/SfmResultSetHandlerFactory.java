package org.simpleflatmapper.sql2o;

import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.sql2o.ResultSetHandler;
import org.sql2o.ResultSetHandlerFactory;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SfmResultSetHandlerFactory<T> implements ResultSetHandlerFactory<T> {
    private final DynamicJdbcMapper<T> mapper;

    public SfmResultSetHandlerFactory(DynamicJdbcMapper<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public ResultSetHandler<T> newResultSetHandler(ResultSetMetaData resultSetMetaData) throws SQLException {
        JdbcMapper<T> staticMapper = mapper.getMapper(resultSetMetaData);
        return new SfmResultSetHandler<T>(staticMapper);
    }


}
