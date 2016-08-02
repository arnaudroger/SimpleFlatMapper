package org.simpleflatmapper.jdbc.impl.setter;


import java.sql.*;

public class DatePreparedStatementIndexSetter implements PreparedStatementIndexSetter<Date> {

    @Override
    public void set(PreparedStatement target, Date value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.DATE);
        } else {
            target.setDate(columnIndex, value);
        }
    }
}
