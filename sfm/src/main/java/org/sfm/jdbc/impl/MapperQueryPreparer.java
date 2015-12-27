package org.sfm.jdbc.impl;

import org.sfm.jdbc.QueryBinder;
import org.sfm.jdbc.QueryPreparer;
import org.sfm.jdbc.named.NamedSqlQuery;
import org.sfm.map.Mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MapperQueryPreparer<T> implements QueryPreparer<T> {
    private final NamedSqlQuery query;
    private final Mapper<T, PreparedStatement> mapper;

    public MapperQueryPreparer(NamedSqlQuery query, Mapper<T, PreparedStatement> mapper) {
        this.query = query;
        this.mapper = mapper;
    }


    @Override
    public QueryBinder<T> prepare(Connection connection) throws SQLException {
        return new MapperQueryBinder<T>(mapper, connection, query);
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(query.toSqlQuery());
    }


}
