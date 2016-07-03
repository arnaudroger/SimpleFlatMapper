package org.sfm.jdbc;

import org.sfm.jdbc.impl.CrudFactory;
import org.sfm.jdbc.impl.CrudMeta;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.meta.AliasProviderFactory;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.Table;

import java.sql.Connection;
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

    public Crud<T, K> table(Connection connection, String table) throws SQLException {
        CrudMeta crudMeta = CrudMeta.of(connection, table, jdbcMapperFactory.columnDefinitions());
        return CrudFactory.newInstance(target, keyTarget, crudMeta, jdbcMapperFactory);
    }


    public Crud<T, K> to(Connection connection) throws SQLException {
        CrudMeta crudMeta = CrudMeta.of(connection, getTable(connection, target), jdbcMapperFactory.columnDefinitions());
        return CrudFactory.newInstance(target, keyTarget, crudMeta, jdbcMapperFactory);
    }

    private String getTable(Connection connection, ClassMeta<T> target) throws SQLException {
        final Class<Object> targetClass = TypeHelper.toClass(target.getType());
        Table table = AliasProviderFactory.getAliasProvider().getTable(targetClass);

        StringBuilder sb = new StringBuilder();
        if (table.schema() != null && table.schema().length() > 0) {
            sb.append(table.schema()).append(".");
        }

        if (table.table() == null) {
            final ResultSet tables = connection.getMetaData().getTables(connection.getCatalog(), null, null, null);
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
}
