package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectQuery<T, P> {
    private final QueryPreparer<P> queryPreparer;
    private final JdbcMapper<T> mapper;

    public SelectQuery(QueryPreparer<P> queryPreparer, JdbcMapper<T> mapper) {
        this.queryPreparer = queryPreparer;
        this.mapper = mapper;
    }

    public T readFirst(Connection connection, P p) throws SQLException {
        PreparedStatement preparedStatement = queryPreparer.prepare(connection).bind(p);
        try {
            ResultSet rs = preparedStatement.executeQuery();
            try {
                if (rs.next()) {
                    return mapper.map(rs);
                }
                return null;
            } finally {
                rs.close();
            }
        } finally {
            preparedStatement.close();
        }

    }

    public <C extends CheckedConsumer<? super T>> C read(Connection connection, P p, C consumer) throws SQLException {
        PreparedStatement preparedStatement = queryPreparer.prepare(connection).bind(p);
        try {
            ResultSet rs = preparedStatement.executeQuery();
            try {
                mapper.forEach(rs, consumer);
            } finally {
                rs.close();
            }
        } finally {
            preparedStatement.close();
        }
        return consumer;
    }
}
