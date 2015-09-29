package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class OrdinalEnumPreparedStatementSetter implements Setter<PreparedStatement, Enum<?>> {
    private final int columnIndex;

    public OrdinalEnumPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Enum<?> value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.INTEGER);
        } else {
            target.setInt(columnIndex, value.ordinal());
        }
    }
}
