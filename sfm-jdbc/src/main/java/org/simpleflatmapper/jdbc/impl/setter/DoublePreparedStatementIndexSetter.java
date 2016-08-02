package org.simpleflatmapper.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DoublePreparedStatementIndexSetter implements PreparedStatementIndexSetter<Double> {


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
