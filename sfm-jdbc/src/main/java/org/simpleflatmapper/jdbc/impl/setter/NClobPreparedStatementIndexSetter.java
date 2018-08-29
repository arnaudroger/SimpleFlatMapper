package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class NClobPreparedStatementIndexSetter implements PreparedStatementIndexSetter<NClob> {

    @Override
    public void set(PreparedStatement target, NClob value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.NCLOB);
        } else {
            target.setNClob(columnIndex, value);
        }
    }
}
