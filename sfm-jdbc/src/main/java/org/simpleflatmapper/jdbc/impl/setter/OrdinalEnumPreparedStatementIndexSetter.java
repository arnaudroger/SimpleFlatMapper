package org.simpleflatmapper.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class OrdinalEnumPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Enum<?>> {

    @Override
    public void set(PreparedStatement target, Enum<?> value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.INTEGER);
        } else {
            target.setInt(columnIndex, value.ordinal());
        }
    }
}
