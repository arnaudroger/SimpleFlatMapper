package org.sfm.jdbc.impl;

import org.sfm.jdbc.PreparedStatementMapper;
import org.sfm.jdbc.named.NamedSqlQuery;
import org.sfm.map.Mapper;
import org.sfm.utils.ErrorHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementMapperDelegate<T> implements PreparedStatementMapper<T> {
    private final NamedSqlQuery query;
    private final Mapper<T, PreparedStatement> mapper;

    public PreparedStatementMapperDelegate(NamedSqlQuery query, Mapper<T, PreparedStatement> mapper) {
        this.query = query;
        this.mapper = mapper;
    }


    @Override
    public PreparedStatement prepare(Connection connection) throws SQLException {
        return connection.prepareStatement(query.toSqlQuery());
    }

    @Override
    public void bind(PreparedStatement ps, T value) throws SQLException {
        try {
            mapper.mapTo(value, ps, null);
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        }
    }

    @Override
    public PreparedStatement prepareAndBind(Connection connection, T value) throws SQLException {
        PreparedStatement ps = prepare(connection);
        bind(ps, value);
        return ps;
    }
 }
