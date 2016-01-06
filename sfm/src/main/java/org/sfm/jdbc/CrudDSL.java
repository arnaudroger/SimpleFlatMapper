package org.sfm.jdbc;

import org.sfm.jdbc.impl.CrudFactory;
import org.sfm.jdbc.impl.CrudMeta;
import org.sfm.jdbc.impl.DefaultCrud;

import java.lang.reflect.Type;
import java.sql.Connection;
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
}
