package org.simpleflatmapper.sql2o;

import org.simpleflatmapper.jdbc.JdbcMapper;
import org.sql2o.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class SfmResultSetHandler<T> implements ResultSetHandler<T> {
        private final JdbcMapper<T> mapper;

        public SfmResultSetHandler(JdbcMapper<T> mapper) {
            this.mapper = mapper;
        }

        @Override
        public T handle(ResultSet resultSet) throws SQLException {
            return mapper.map(resultSet);
        }
    }