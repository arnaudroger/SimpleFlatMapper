package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;
import org.simpleflatmapper.jdbc.QueryBinder;
import org.simpleflatmapper.jdbc.SizeSupplier;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.simpleflatmapper.core.utils.Asserts;
import org.simpleflatmapper.core.utils.ErrorHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MultiIndexQueryBinder<T> implements QueryBinder<T> {
    private final NamedSqlQuery query;
    private final MultiIndexFieldMapper<T>[] fields;
    private final Connection connection;
    private final String[] generatedKeys;


    protected MultiIndexQueryBinder(NamedSqlQuery query, MultiIndexFieldMapper<T>[] fields, String[] generatedKeys, Connection connection) {
        this.connection = connection;
        this.query = Asserts.requireNonNull("query", query);
        this.fields = Asserts.requireNonNull("fields", fields);
        this.generatedKeys = generatedKeys;
    }

    @Override
    public PreparedStatement bind(T value) throws SQLException {
        PreparedStatement ps = createPreparedStatement(value);
        try {
            int columnIndex = 0;
            for(int i = 0; i < fields.length; i++) {
                columnIndex += fields[i].map(ps, value, columnIndex);
            }
            return ps;
        } catch (Exception e) {
            try  {
                ps.close();
            } catch(SQLException sqle) {
                // IGNORE
            }
            ErrorHelper.rethrow(e);
            return null;
        }

    }

    @Override
    public void bindTo(T value, PreparedStatement ps) throws SQLException {
        throw new UnsupportedOperationException();
    }

    private PreparedStatement createPreparedStatement(final T value) throws SQLException {
        String sql = query.toSqlQuery(new SizeSupplier() {
            @Override
            public int getSize(int columnIndex) {
                return fields[columnIndex].getSize(value);
            }
        });

        if (generatedKeys != null && generatedKeys.length > 0) {
            return connection.prepareStatement(sql, generatedKeys);
        } else {
            return connection.prepareStatement(sql);
        }
    }

}
