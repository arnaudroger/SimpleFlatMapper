package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.map.context.KeySourceGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcKeySourceGetter implements KeySourceGetter<JdbcColumnKey, ResultSet> {
    
    public static final JdbcKeySourceGetter INSTANCE = new JdbcKeySourceGetter();
    
    private JdbcKeySourceGetter() {
    }
    
    @Override
    public Object getValue(JdbcColumnKey key, ResultSet source) throws SQLException {
        return source.getObject(key.getIndex());
    }
}
