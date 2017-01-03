package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;
import org.simpleflatmapper.jdbc.QueryBinder;
import org.simpleflatmapper.jdbc.QueryPreparer;
import org.simpleflatmapper.jdbc.SizeSupplier;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.util.Asserts;
import org.simpleflatmapper.util.ErrorHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MultiIndexQueryPreparer<T> implements QueryPreparer<T> {


    private final NamedSqlQuery query;
    private final MultiIndexFieldMapper<T>[] multiIndexFieldMappers;
    private final String[] generatedKeys;

    public MultiIndexQueryPreparer(NamedSqlQuery query, MultiIndexFieldMapper<T>[] multiIndexFieldMappers, String[] generatedKeys) {
        this.query = query;
        this.multiIndexFieldMappers = multiIndexFieldMappers;
        this.generatedKeys = generatedKeys;
    }

    @Override
    public QueryBinder<T> prepare(Connection connection) throws SQLException {
        return new MultiIndexQueryBinder(connection);
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mapper<T, PreparedStatement> mapper() {
        throw new UnsupportedOperationException();
    }


    @Override
    public String toRewrittenSqlQuery(final T value) {
        return query.toSqlQuery(new SizeSupplier() {
            @Override
            public int getSize(int columnIndex) {
                return multiIndexFieldMappers[columnIndex].getSize(value);
            }
        });
    }


    public class MultiIndexQueryBinder implements QueryBinder<T> {
        private final Connection connection;


        protected MultiIndexQueryBinder(Connection connection) {
            this.connection = connection;
        }

        @Override
        public PreparedStatement bind(T value) throws SQLException {
            PreparedStatement ps = createPreparedStatement(value);
            try {
                int columnIndex = 0;
                for (int i = 0; i < multiIndexFieldMappers.length; i++) {
                    columnIndex += multiIndexFieldMappers[i].map(ps, value, columnIndex);
                }
                return ps;
            } catch (Exception e) {
                try {
                    ps.close();
                } catch (SQLException sqle) {
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
            String sql = toRewrittenSqlQuery(value);

            if (generatedKeys != null && generatedKeys.length > 0) {
                return connection.prepareStatement(sql, generatedKeys);
            } else {
                return connection.prepareStatement(sql);
            }
        }

    }
}
