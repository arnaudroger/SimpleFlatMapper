package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextFactory;
import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;
import org.simpleflatmapper.jdbc.QueryBinder;
import org.simpleflatmapper.jdbc.QueryPreparer;
import org.simpleflatmapper.jdbc.SizeSupplier;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.util.ErrorHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MultiIndexQueryPreparer<T> implements QueryPreparer<T> {


    private final NamedSqlQuery query;
    private final MultiIndexFieldMapper<T>[] multiIndexFieldMappers;
    private final String[] generatedKeys;
    private final ContextFactory contextFactory;

    public MultiIndexQueryPreparer(NamedSqlQuery query, MultiIndexFieldMapper<T>[] multiIndexFieldMappers, String[] generatedKeys, ContextFactory contextFactory) {
        this.query = query;
        this.multiIndexFieldMappers = multiIndexFieldMappers;
        this.generatedKeys = generatedKeys;
        this.contextFactory = contextFactory;
    }

    @Override
    public QueryBinder<T> prepare(Connection connection) throws SQLException {
        return new MultiIndexQueryBinder(connection, contextFactory);
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldMapper<T, PreparedStatement> mapper() {
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
        private final ContextFactory contextFactory;


        protected MultiIndexQueryBinder(Connection connection, ContextFactory contextFactory) {
            this.connection = connection;
            this.contextFactory = contextFactory;
        }

        @Override
        public PreparedStatement bind(T value) throws SQLException {
            PreparedStatement ps = createPreparedStatement(value);
            Context context = contextFactory.newContext();
            try {
                int columnIndex = 0;
                for (int i = 0; i < multiIndexFieldMappers.length; i++) {
                    columnIndex += multiIndexFieldMappers[i].map(ps, value, columnIndex, context);
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
