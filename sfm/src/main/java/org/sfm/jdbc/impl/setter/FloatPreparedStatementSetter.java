package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.FloatSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class FloatPreparedStatementSetter implements Setter<PreparedStatement, Float>, FloatSetter<PreparedStatement> {
    private final int columnIndex;

    public FloatPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Float value) throws SQLException {
        if (value != null) {
            target.setFloat(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.FLOAT);
        }
    }

    @Override
    public void setFloat(PreparedStatement target, float value) throws Exception {
        target.setFloat(columnIndex, value);
    }
}
