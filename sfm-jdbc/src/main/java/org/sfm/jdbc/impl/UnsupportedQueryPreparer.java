package org.sfm.jdbc.impl;

import org.sfm.jdbc.QueryBinder;
import org.sfm.map.Mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UnsupportedQueryPreparer<T> implements org.sfm.jdbc.QueryPreparer<T> {

    private final String message;

    public UnsupportedQueryPreparer(String message) {
        this.message = message;
    }

    @Override
    public QueryBinder<T> prepare(Connection connection) throws SQLException {
        throw new UnsupportedOperationException(message);
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        throw new UnsupportedOperationException(message);
    }

    @Override
    public Mapper<T, PreparedStatement> mapper() {
        throw new UnsupportedOperationException(message);
    }
}
