package org.sfm.jdbc;

import org.sfm.map.impl.context.MappingContextFactoryBuilder;
import org.sfm.map.impl.context.KeySourceGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcMappingContextFactoryBuilder extends MappingContextFactoryBuilder<ResultSet, JdbcColumnKey> {
    public JdbcMappingContextFactoryBuilder() {
        super(new JdbcKeySourceGetter());
    }

    private static class JdbcKeySourceGetter implements KeySourceGetter<JdbcColumnKey, ResultSet> {
        @Override
        public Object getValue(JdbcColumnKey key, ResultSet source) throws SQLException {
            return source.getObject(key.getIndex());
        }
    }
}
