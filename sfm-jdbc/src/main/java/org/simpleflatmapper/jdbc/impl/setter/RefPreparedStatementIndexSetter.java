package org.simpleflatmapper.jdbc.impl.setter;

import java.sql.Ref;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class RefPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Ref> {
    @Override
    public void set(PreparedStatement target, Ref value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.REF);
        } else {
            target.setRef(columnIndex, value);
        }
    }
}
