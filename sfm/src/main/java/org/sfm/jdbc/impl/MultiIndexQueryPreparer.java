package org.sfm.jdbc.impl;

import org.sfm.jdbc.QueryBinder;
import org.sfm.jdbc.QueryPreparer;
import org.sfm.jdbc.named.NamedSqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MultiIndexQueryPreparer<T> implements QueryPreparer<T> {


    private final NamedSqlQuery query;
    private final MultiIndexFieldMapper<T, ?>[] multiIndexFieldMappers;

    public MultiIndexQueryPreparer(NamedSqlQuery query, MultiIndexFieldMapper<T, ?>[] multiIndexFieldMappers) {
        this.query = query;
        this.multiIndexFieldMappers = multiIndexFieldMappers;
    }

    @Override
    public QueryBinder<T> prepare(Connection connection) throws SQLException {
        return new MultiIndexQueryBinder<T>(query, multiIndexFieldMappers, connection);
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
