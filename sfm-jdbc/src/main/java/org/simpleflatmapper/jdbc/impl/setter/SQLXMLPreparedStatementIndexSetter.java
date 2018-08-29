package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Types;

public class SQLXMLPreparedStatementIndexSetter implements PreparedStatementIndexSetter<SQLXML> {
    @Override
    public void set(PreparedStatement target, SQLXML value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.SQLXML);
        } else {
            target.setSQLXML(columnIndex, value);
        }
    }
}
