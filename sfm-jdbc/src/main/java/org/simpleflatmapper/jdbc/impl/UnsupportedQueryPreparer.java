package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.QueryBinder;
import org.simpleflatmapper.core.map.Mapper;
import org.simpleflatmapper.jdbc.QueryPreparer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UnsupportedQueryPreparer<T> implements QueryPreparer<T> {

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
