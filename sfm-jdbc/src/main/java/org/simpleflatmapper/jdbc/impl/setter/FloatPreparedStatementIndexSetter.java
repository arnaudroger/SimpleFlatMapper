package org.simpleflatmapper.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class FloatPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Float> {

    @Override
    public void set(PreparedStatement target, Float value, int columnIndex) throws SQLException {
        if (value != null) {
            target.setFloat(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.FLOAT);
        }
    }

    public void setFloat(PreparedStatement target, float value, int columnIndex) throws Exception {
        target.setFloat(columnIndex, value);
    }
}
