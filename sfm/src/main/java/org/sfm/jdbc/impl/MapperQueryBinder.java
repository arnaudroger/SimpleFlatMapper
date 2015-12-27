package org.sfm.jdbc.impl;

import org.sfm.jdbc.QueryBinder;
import org.sfm.jdbc.named.NamedSqlQuery;
import org.sfm.map.Mapper;
import org.sfm.utils.ErrorHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MapperQueryBinder<T> implements QueryBinder<T> {
    private final Mapper<T, PreparedStatement> mapper;
    private final Connection connection;
    private final NamedSqlQuery sqlQuery;

    public MapperQueryBinder(Mapper<T, PreparedStatement> mapper, Connection connection, NamedSqlQuery sqlQuery) {
        this.mapper = mapper;
        this.connection = connection;
        this.sqlQuery = sqlQuery;
    }

    @Override
    public PreparedStatement bind(T value) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery.toSqlQuery());
        try {
            mapper.mapTo(value, preparedStatement, null);
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
            mapper.mapTo(value, ps, null);
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        }
    }
}
