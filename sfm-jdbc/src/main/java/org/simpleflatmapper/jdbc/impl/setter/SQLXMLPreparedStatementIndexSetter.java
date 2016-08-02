package org.simpleflatmapper.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Types;

public class SQLXMLPreparedStatementIndexSetter implements PreparedStatementIndexSetter<SQLXML> {
    @Override
    public void set(PreparedStatement target, SQLXML value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.SQLXML);
        } else {
            target.setSQLXML(columnIndex, value);
        }
    }
}
