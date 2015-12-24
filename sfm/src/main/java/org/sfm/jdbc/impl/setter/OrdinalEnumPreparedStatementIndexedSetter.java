package org.sfm.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class OrdinalEnumPreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<Enum<?>> {

    @Override
    public void set(PreparedStatement target, Enum<?> value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.INTEGER);
        } else {
            target.setInt(columnIndex, value.ordinal());
        }
    }
}
