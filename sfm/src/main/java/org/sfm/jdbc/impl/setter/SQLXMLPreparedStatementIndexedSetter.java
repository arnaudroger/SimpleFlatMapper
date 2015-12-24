package org.sfm.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Types;

public class SQLXMLPreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<SQLXML> {
    @Override
    public void set(PreparedStatement target, SQLXML value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.SQLXML);
        } else {
            target.setSQLXML(columnIndex, value);
        }
    }
}
