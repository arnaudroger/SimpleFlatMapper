package org.sfm.jdbc.impl;

import org.sfm.jdbc.PreparedStatementMapper;
import org.sfm.jdbc.SizeSupplier;
import org.sfm.jdbc.named.NamedSqlQuery;
import org.sfm.utils.Asserts;
import org.sfm.utils.ErrorHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementMultiIndexMapper<T> implements PreparedStatementMapper<T> {
    private final NamedSqlQuery query;

    private final MultiIndexFieldMapper<T, ?>[] fields;


    public PreparedStatementMultiIndexMapper(NamedSqlQuery query, MultiIndexFieldMapper<T, ?>[] fields) {
        this.query = Asserts.requireNonNull("query", query);
        this.fields = Asserts.requireNonNull("fields", fields);
    }


    @Override
    public PreparedStatement prepare(Connection connection) throws SQLException {
        throw new UnsupportedOperationException("Unknown Collection/Array size need to used prepareAndBind");
    }

    @Override
    public void bind(PreparedStatement ps, T value) throws SQLException {
        throw new UnsupportedOperationException("Unknown Collection/Array size need to used prepareAndBind");
    }

    @Override
    public PreparedStatement prepareAndBind(Connection connection, T value) throws SQLException {
        PreparedStatement ps = _prepare(connection, value);
        _bind(ps, value);
        return ps;
    }

    private void _bind(PreparedStatement ps, T value) {
        try {
            int columnIndex = 0;

            for(int i = 0; i < fields.length; i++) {
                columnIndex += fields[i].map(ps, value, columnIndex);
            }

        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        }

    }

    private PreparedStatement _prepare(Connection connection, T value) throws SQLException {
        String sql = query.toSqlQuery(new SizeSupplier() {
            @Override
            public int getSize(int columnIndex) {
                return fields[columnIndex].getSize(value);
            }
        });

        return connection.prepareStatement(sql);
    }

}
