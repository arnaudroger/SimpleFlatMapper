package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.QueryPreparer;
import org.simpleflatmapper.jdbc.SelectQuery;
import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectQueryImpl<T, P> implements SelectQuery<T, P> {
    private final QueryPreparer<P> queryPreparer;
    private final JdbcMapper<T> mapper;

    public SelectQueryImpl(QueryPreparer<P> queryPreparer, JdbcMapper<T> mapper) {
        this.queryPreparer = queryPreparer;
        this.mapper = mapper;
    }

    @Override
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
                safeClose(rs);
            }
        } finally {
            safeClose(preparedStatement);
        }

    }

    @Override
    public <C extends CheckedConsumer<? super T>> C read(Connection connection, P p, C consumer) throws SQLException {
        PreparedStatement preparedStatement = queryPreparer.prepare(connection).bind(p);
        try {
            ResultSet rs = preparedStatement.executeQuery();
            try {
                mapper.forEach(rs, consumer);
            } finally {
                safeClose(rs);
            }
        } finally {
            safeClose(preparedStatement);
        }
        return consumer;
    }

    private void safeClose(PreparedStatement preparedStatement) {
        try {
            preparedStatement.close();
        } catch (SQLException e) {}
    }

    private void safeClose(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {}
    }

}
