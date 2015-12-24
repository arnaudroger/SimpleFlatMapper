package org.sfm.jdbc.impl.setter;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BigDecimalPreparedStatementIndexedSetter implements PrepareStatementIndexedSetter<BigDecimal> {
    @Override
    public void set(PreparedStatement target, BigDecimal value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.NUMERIC);
        } else {
            target.setBigDecimal(columnIndex, value);
        }
    }
}
