package org.simpleflatmapper.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BooleanPreparedStatementIndexSetter implements
        PreparedStatementIndexSetter<Boolean> {

    @Override
    public void set(PreparedStatement target, Boolean value, int columnIndex) throws SQLException {
        if (value != null) {
            target.setBoolean(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.BOOLEAN);
        }
    }

    public void setBoolean(PreparedStatement target, boolean value, int columnIndex) throws Exception {
        target.setBoolean(columnIndex, value);
    }
}
