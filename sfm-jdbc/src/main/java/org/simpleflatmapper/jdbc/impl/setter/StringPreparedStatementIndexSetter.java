package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class StringPreparedStatementIndexSetter implements PreparedStatementIndexSetter<String> {
    @Override
    public void set(PreparedStatement target, String value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.VARCHAR);
        } else {
            target.setString(columnIndex, value);
        }
    }
}
