package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class UUIDStringPreparedStatementIndexSetter implements PreparedStatementIndexSetter<UUID> {
    @Override
    public void set(PreparedStatement ps, UUID value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            ps.setNull(columnIndex, Types.VARCHAR);
        } else {
            ps.setString(columnIndex, value.toString());
        }
    }
}
