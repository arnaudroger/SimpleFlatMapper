package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.QueryPreparer;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SelectQueryWhereFactory<T> {

    private final String table;
    private final JdbcMapperFactory jdbcMapperFactory;
    private final JdbcMapper<T> jdbcMapper;

    private final ConcurrentMap<SelectQueryKey, SelectQueryImpl<T, ?>> cache = new ConcurrentHashMap<SelectQueryKey, SelectQueryImpl<T, ?>>();

    public SelectQueryWhereFactory(String table, JdbcMapper<T> jdbcMapper, JdbcMapperFactory jdbcMapperFactory) {
        this.table = table;
        this.jdbcMapper = jdbcMapper;
        this.jdbcMapperFactory = jdbcMapperFactory;
    }

    @SuppressWarnings("unchecked")
    public <P> SelectQueryImpl<T, P> where(String whereClause, Type paramClass) {
        SelectQueryKey key = new SelectQueryKey(whereClause, paramClass);

        SelectQueryImpl<T, ?> selectQuery = cache.get(key);

        if (selectQuery == null) {
            SelectQueryImpl<T, P> newSelectQuery = newSelectQuery(whereClause, paramClass);
            selectQuery = cache.putIfAbsent(key, newSelectQuery);

            if (selectQuery == null) {
                selectQuery = newSelectQuery;
            }
        }

        return (SelectQueryImpl<T, P>) selectQuery;
    }

    private <P> SelectQueryImpl<T, P> newSelectQuery(String whereClause, Type paramClass) {
        String query = sqlQuery(whereClause);
        QueryPreparer<P> queryPreparer = jdbcMapperFactory.<P>from(paramClass).to(NamedSqlQuery.parse(query));
        return new SelectQueryImpl<T, P>(queryPreparer, jdbcMapper);
    }

    private String sqlQuery(String whereClause) {
        return "SELECT * FROM " + table + " WHERE " + whereClause;
    }

    private static class SelectQueryKey {
        private final String query;
        private final Type type;

        private SelectQueryKey(String query, Type type) {
            this.query = query;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SelectQueryKey that = (SelectQueryKey) o;

            if (!query.equals(that.query)) return false;
            return type.equals(that.type);

        }

        @Override
        public int hashCode() {
            int result = query.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }
    }
}
