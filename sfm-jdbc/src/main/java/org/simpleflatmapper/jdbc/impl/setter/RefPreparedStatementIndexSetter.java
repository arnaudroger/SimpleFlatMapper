package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.Ref;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class RefPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Ref> {
    @Override
    public void set(PreparedStatement target, Ref value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.REF);
        } else {
            target.setRef(columnIndex, value);
        }
    }
}
