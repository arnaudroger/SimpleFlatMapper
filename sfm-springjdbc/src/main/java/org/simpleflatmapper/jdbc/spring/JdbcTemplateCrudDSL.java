package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTemplateCrudDSL<T, K> {
    private final Type keyTarget;
    private final Type target;
    private final JdbcTemplateMapperFactory jdbcTemplateMapperFactory;

    public JdbcTemplateCrudDSL(JdbcTemplateMapperFactory jdbcTemplateMapperFactory, Type target, Type keyTarget) {
        this.jdbcTemplateMapperFactory = jdbcTemplateMapperFactory;
        this.target = target;
        this.keyTarget = keyTarget;
    }

    public JdbcTemplateCrud<T, K> to(JdbcOperations jdbcOperations, final String table) {
        final JdbcMapperFactory factory = JdbcMapperFactory.newInstance(jdbcTemplateMapperFactory);

        Crud<T, K> crud =
            jdbcOperations.execute(new ConnectionCallback<Crud<T, K>>() {
                @Override
                public Crud<T, K> doInConnection(Connection connection) throws SQLException, DataAccessException {
                    return factory.<T, K>crud(target, keyTarget).table(connection, table);
                }
            });

        return new JdbcTemplateCrud<T, K>(jdbcOperations, crud);
    }

    public JdbcTemplateCrud<T, K> to(JdbcOperations jdbcOperations) {
        final JdbcMapperFactory factory = JdbcMapperFactory.newInstance(jdbcTemplateMapperFactory);

        Crud<T, K> crud =
                jdbcOperations.execute(new ConnectionCallback<Crud<T, K>>() {
                    @Override
                    public Crud<T, K> doInConnection(Connection connection) throws SQLException, DataAccessException {
                        return factory.<T, K>crud(target, keyTarget).to(connection);
                    }
                });

        return new JdbcTemplateCrud<T, K>(jdbcOperations, crud);
    }


    public JdbcTemplateCrud<T, K> lazilyTo(JdbcOperations jdbcOperations, final String table) {
        final JdbcMapperFactory factory = JdbcMapperFactory.newInstance(jdbcTemplateMapperFactory);
        Crud<T, K> crud = factory.<T, K>crud(target, keyTarget).table(table);
        return new JdbcTemplateCrud<T, K>(jdbcOperations, crud);
    }

    public JdbcTemplateCrud<T, K> lazilyTo(JdbcOperations jdbcOperations) {
        final JdbcMapperFactory factory = JdbcMapperFactory.newInstance(jdbcTemplateMapperFactory);
        Crud<T, K> crud = factory.<T, K>crud(target, keyTarget).crud();
        return new JdbcTemplateCrud<T, K>(jdbcOperations, crud);
    }
}
