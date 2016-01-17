package org.sfm.jdbc;

import org.sfm.jdbc.impl.CrudFactory;
import org.sfm.jdbc.impl.CrudMeta;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.meta.AliasProviderFactory;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyNameMatcher;
import org.sfm.reflect.meta.Table;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrudDSL<T, K> {
    private final Type target;
    private final Type keyTarget;
    private final JdbcMapperFactory jdbcMapperFactory;

    public CrudDSL(Type target, Type keyTarget, JdbcMapperFactory jdbcMapperFactory) {
        this.target = target;
        this.keyTarget = keyTarget;
        this.jdbcMapperFactory = jdbcMapperFactory;
    }

    public Crud<T, K> table(Connection connection, String table) throws SQLException {
        CrudMeta<T, K> crudMeta = CrudMeta.of(connection, table);
        return CrudFactory.newInstance(target, keyTarget, crudMeta, jdbcMapperFactory);
    }


    public Crud<T, K> to(Connection connection) throws SQLException {
        CrudMeta<T, K> crudMeta = CrudMeta.of(connection, getTable(connection, target));
        return CrudFactory.newInstance(target, keyTarget, crudMeta, jdbcMapperFactory);
    }

    private String getTable(Connection connection, Type target) throws SQLException {
        Table table = AliasProviderFactory.getAliasProvider().getTable(TypeHelper.toClass(target));

        StringBuilder sb = new StringBuilder();
        if (table.schema() != null && table.schema().length() > 0) {
            sb.append(table.schema()).append(".");
        }

        if (table.table() == null) {
            final ResultSet tables = connection.getMetaData().getTables(connection.getCatalog(), null, null, null);
            final String className = TypeHelper.toClass(target).getSimpleName();
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
            throw new IllegalArgumentException("Type " + target + " has no table mapping");
        }
        sb.append(table.table());

        return sb.toString();
    }
}
