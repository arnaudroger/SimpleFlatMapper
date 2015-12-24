package org.sfm.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class StringEnumPreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<Enum<?>> {
    @Override
    public void set(PreparedStatement target, Enum<?> value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.VARCHAR);
        } else {
            target.setString(columnIndex, value.name());
        }
    }
}
