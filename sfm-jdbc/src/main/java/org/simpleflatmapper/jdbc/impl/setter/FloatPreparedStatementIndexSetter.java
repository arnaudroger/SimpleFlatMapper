package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class FloatPreparedStatementIndexSetter implements PreparedStatementIndexSetter<Float> {

    @Override
    public void set(PreparedStatement target, Float value, int columnIndex, Context context) throws SQLException {
        if (value != null) {
            target.setFloat(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.FLOAT);
        }
    }

    public void setFloat(PreparedStatement target, float value, int columnIndex, Context context) throws Exception {
        target.setFloat(columnIndex, value);
    }
}
