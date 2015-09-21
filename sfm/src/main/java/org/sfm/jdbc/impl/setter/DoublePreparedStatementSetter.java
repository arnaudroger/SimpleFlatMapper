package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.DoubleSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DoublePreparedStatementSetter implements Setter<PreparedStatement, Double>, DoubleSetter<PreparedStatement> {
    private final int columnIndex;

    public DoublePreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Double value) throws SQLException {
        if (value != null) {
            target.setDouble(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.DOUBLE);
        }
    }

    @Override
    public void setDouble(PreparedStatement target, double value) throws Exception {
        target.setDouble(columnIndex, value);
    }
}
