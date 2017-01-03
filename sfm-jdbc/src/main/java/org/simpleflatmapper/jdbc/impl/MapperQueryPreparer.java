package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.QueryBinder;
import org.simpleflatmapper.jdbc.QueryPreparer;
import org.simpleflatmapper.jdbc.SizeSupplier;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.simpleflatmapper.map.Mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MapperQueryPreparer<T> implements QueryPreparer<T> {
    private final NamedSqlQuery query;
    private final Mapper<T, PreparedStatement> mapper;
    private final String[] generatedKeys;

    public MapperQueryPreparer(NamedSqlQuery query, Mapper<T, PreparedStatement> mapper, String[] generatedKeys) {
        this.query = query;
        this.mapper = mapper;
        this.generatedKeys = generatedKeys;
    }


    @Override
    public QueryBinder<T> prepare(Connection connection) throws SQLException {
        return new MapperQueryBinder<T>(this, connection);
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        return generatedKeys != null ? connection.prepareStatement(query.toSqlQuery(), generatedKeys) : connection.prepareStatement(query.toSqlQuery());
    }

    @Override
    public Mapper<T, PreparedStatement> mapper() {
        return mapper;
    }

    @Override
    public String toRewrittenSqlQuery(final T value) {
        return query.toSqlQuery();
    }


}
