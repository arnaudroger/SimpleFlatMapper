package org.simpleflatmapper.jdbi;

import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.QueryPreparer;
import org.simpleflatmapper.jdbc.SqlTypeColumnProperty;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.simpleflatmapper.util.ErrorHelper;
import org.skife.jdbi.v2.Binding;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.tweak.RewrittenStatement;
import org.skife.jdbi.v2.tweak.StatementRewriter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SfmBinderFactory implements BinderFactory<SfmBind> {

    public Binder build(SfmBind annotation) {
        return new SfmBinder();
    }

    private static class SfmBinder<T> implements Binder<SfmBind, T> {
        private final ConcurrentMap<QueryPreparerKey, QueryPreparer<T>> cache = new ConcurrentHashMap<QueryPreparerKey, QueryPreparer<T>>();

        @Override
        public void bind(SQLStatement<?> sqlStatement, SfmBind annotation, T o) {
            QueryPreparer<T> queryPreparer = getQueryPreparer(sqlStatement, annotation, o.getClass());
            sqlStatement.setStatementRewriter(new SfmStatementRewriter<T>(queryPreparer, o));
        }

        private QueryPreparer<T> getQueryPreparer(SQLStatement<?> sqlStatement, SfmBind annotation, Class<?> aClass) {
            QueryPreparerKey key = new QueryPreparerKey(sqlStatement.getContext().getRawSql(), aClass);

            QueryPreparer<T> queryPreparer = cache.get(key);

            if (queryPreparer == null) {
                NamedSqlQuery parse = NamedSqlQuery.parse(sqlStatement.getContext().getRawSql());

                JdbcMapperFactory jdbcMapperFactory = JdbcMapperFactory
                        .newInstance();

                for (SqlType col : annotation.sqlTypes()) {
                    jdbcMapperFactory.addColumnProperty(col.name(), SqlTypeColumnProperty.of(col.type()));
                }

                queryPreparer = jdbcMapperFactory.<T>from(aClass).to(parse);

                QueryPreparer<T> cachedQP = cache.putIfAbsent(key, queryPreparer);

                if (cachedQP != null) {
                    queryPreparer = cachedQP;
                }
            }

            return queryPreparer;
        }
    }

    private static class QueryPreparerKey {
        private final String sql;
        private final Class<?> target;

        private QueryPreparerKey(String sql, Class<?> target) {
            this.sql = sql;
            this.target = target;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QueryPreparerKey that = (QueryPreparerKey) o;

            if (!sql.equals(that.sql)) return false;
            return target.equals(that.target);
        }

        @Override
        public int hashCode() {
            int result = sql.hashCode();
            result = 31 * result + target.hashCode();
            return result;
        }
    }

    private static class SfmStatementRewriter<T> implements StatementRewriter {
        private final QueryPreparer<T> queryPreparer;
        private final T o;

        public SfmStatementRewriter(QueryPreparer<T> queryPreparer, T o) {
            this.queryPreparer = queryPreparer;
            this.o = o;
        }

        @Override
        public RewrittenStatement rewrite(String s, Binding binding, StatementContext statementContext) {
            final String sql = queryPreparer.toRewrittenSqlQuery(o);

            return new SfmRewrittenStatement(sql);
        }

        private class SfmRewrittenStatement implements RewrittenStatement {
            private final String sql;

            public SfmRewrittenStatement(String sql) {
                this.sql = sql;
            }

            @Override
            public void bind(Binding binding, PreparedStatement preparedStatement) throws SQLException {
                try {
                    queryPreparer.mapper().mapTo(o, preparedStatement, null);
                } catch (Exception e) {
                    ErrorHelper.rethrow(e);
                }
            }

            @Override
            public String getSql() {
                return sql;
            }
        }
    }
}
