package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.util.UUIDHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class UUIDBinaryPreparedStatementIndexSetter implements PreparedStatementIndexSetter<UUID> {
    @Override
    public void set(PreparedStatement ps, UUID value, int columnIndex) throws SQLException {
        if (value == null) {
            ps.setNull(columnIndex, Types.BINARY);
        } else {
            ps.setBytes(columnIndex, UUIDHelper.toBytes(value));
        }
    }
}
