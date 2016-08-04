package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.QueryBinder;
import org.simpleflatmapper.util.ErrorHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MapperQueryBinder<T> implements QueryBinder<T> {
    private final MapperQueryPreparer<T> queryPreparer;
    private final Connection connection;

    public MapperQueryBinder(MapperQueryPreparer<T> queryPreparer, Connection connection) {
        this.queryPreparer = queryPreparer;
        this.connection = connection;
    }

    @Override
    public PreparedStatement bind(T value) throws SQLException {
        PreparedStatement preparedStatement = queryPreparer.prepareStatement(connection);
        try {
            queryPreparer.mapper().mapTo(value, preparedStatement, null);
            return preparedStatement;
        } catch(Exception t) {
            try {
                preparedStatement.close();
            } catch(SQLException e) {
                // IGNORE
            }
            ErrorHelper.rethrow(t);
            return null;
        }
    }


    @Override
    public void bindTo(T value, PreparedStatement ps) throws SQLException {
        try {
            queryPreparer.mapper().mapTo(value, ps, null);
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        }
    }
}
