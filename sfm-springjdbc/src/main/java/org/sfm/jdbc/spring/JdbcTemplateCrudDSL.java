package org.sfm.jdbc.spring;

import org.sfm.jdbc.Crud;
import org.sfm.jdbc.JdbcMapperFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

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

    public JdbcTemplateCrud<T, K> to(JdbcTemplate jdbcTemplate, final String table) {
        final JdbcMapperFactory factory = JdbcMapperFactory.newInstance(jdbcTemplateMapperFactory);

        Crud<T, K> crud =
            jdbcTemplate.execute(new ConnectionCallback<Crud<T, K>>() {
                @Override
                public Crud<T, K> doInConnection(Connection connection) throws SQLException, DataAccessException {
                    return factory.<T, K>crud(target, keyTarget).table(connection, table);
                }
            });

        return new JdbcTemplateCrud<T, K>(jdbcTemplate, crud);
    }
}
