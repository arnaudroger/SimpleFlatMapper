package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Types;

public class SQLXMLPreparedStatementSetter implements Setter<PreparedStatement, SQLXML> {
    private final int columnIndex;

    public SQLXMLPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, SQLXML value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.SQLXML);
        } else {
            target.setSQLXML(columnIndex, value);
        }
    }
}
