package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DoublePreparedStatementIndexSetter implements PreparedStatementIndexSetter<Double> {


    @Override
    public void set(PreparedStatement target, Double value, int columnIndex, Context context) throws SQLException {
        if (value != null) {
            target.setDouble(columnIndex, value);
        } else {
            target.setNull(columnIndex, Types.DOUBLE);
        }
    }

    public void setDouble(PreparedStatement target, double value, int columnIndex, Context context) throws Exception {
        target.setDouble(columnIndex, value);
    }
}
