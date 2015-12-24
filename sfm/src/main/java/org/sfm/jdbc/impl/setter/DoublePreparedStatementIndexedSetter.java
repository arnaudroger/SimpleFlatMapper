package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.DoubleSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DoublePreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<Double> {


    @Override
    public void set(PreparedStatement target, Double value, int columnIndex) throws SQLException {
        if (value != null) {
            target.setDouble(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.DOUBLE);
        }
    }

    public void setDouble(PreparedStatement target, double value, int columnIndex) throws Exception {
        target.setDouble(columnIndex, value);
    }
}
