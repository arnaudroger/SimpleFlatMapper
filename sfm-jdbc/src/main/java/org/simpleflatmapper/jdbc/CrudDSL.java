package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.jdbc.impl.CrudFactory;
import org.simpleflatmapper.jdbc.impl.CrudMeta;
import org.simpleflatmapper.jdbc.impl.LazyCrud;
import org.simpleflatmapper.jdbc.impl.TransactionFactory;
import org.simpleflatmapper.reflect.meta.AliasProviderService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.Table;
import org.simpleflatmapper.util.TypeHelper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrudDSL<T, K> {
    private final ClassMeta<T> target;
    private final ClassMeta<K> keyTarget;
    private final JdbcMapperFactory jdbcMapperFactory;

    public CrudDSL(ClassMeta<T> target, ClassMeta<K> keyTarget, JdbcMapperFactory jdbcMapperFactory) {
        this.target = target;
        this.keyTarget = keyTarget;
        this.jdbcMapperFactory = jdbcMapperFactory;
    }

    public Crud<T, K> lazy() throws SQLException {
        return new LazyCrud<T, K>(this, null);
    }

    public Crud<T, K> table(String table) throws SQLException {
        return new LazyCrud<T, K>(this, table);
    }

    public Crud<T, K> table(Connection connection, String table) throws SQLException {
        CrudMeta crudMeta = CrudMeta.of(connection, table, jdbcMapperFactory.columnDefinitions());
        return CrudFactory.<T, K>newInstance(target, keyTarget, crudMeta, jdbcMapperFactory);
    }

    public ConnectedCrud<T, K> table(DataSource dataSource, String table) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            return new ConnectedCrud<T, K>(new TransactionFactory(dataSource), table(connection, table));
        } finally {
            connection.close();
        }
    }

    public ConnectedCrud<T, K> to(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            return new ConnectedCrud<T, K>(new TransactionFactory(dataSource), to(connection));
        } finally {
            connection.close();
        }
    }


    public Crud<T, K> to(Connection connection) throws SQLException {
        return table(connection, getTable(connection, target));
    }

    private String getTable(Connection connection, ClassMeta<T> target) throws SQLException {
        final Class<Object> targetClass = TypeHelper.toClass(target.getType());
        Table table = AliasProviderService.getAliasProvider().getTable(targetClass);

        StringBuilder sb = new StringBuilder();
        if (table.schema() != null && table.schema().length() > 0) {
            sb.append(table.schema()).append(".");
        }

        if (table.table() == null) {
            DatabaseMetaData metaData = connection.getMetaData();
            final ResultSet tables = getTables(connection, metaData);
            final String className = TypeHelper.toClass(targetClass).getSimpleName();
            try {
                while(tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    if (DefaultPropertyNameMatcher.of(tableName).matches(className)) {
                        sb.append(tableName);
                        return sb.toString();
                    }
                }
            } finally {
                tables.close();
            }
            throw new IllegalArgumentException("Type " + target.getType() + " has no table mapping");
        }
        sb.append(table.table());

        return sb.toString();
    }

    private ResultSet getTables(Connection connection, DatabaseMetaData metaData) throws SQLException {
        try {
            return metaData.getTables(connection.getCatalog(), null, null, null);
        } catch (SQLException e) {
            if ("S1009".equals(e.getSQLState())) { // see https://bugs.mysql.com/bug.php?id=81105
                return metaData.getTables(connection.getCatalog(), null, "", null);
            }
            throw e;
        }
    }
}
