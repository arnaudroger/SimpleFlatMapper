package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BigDecimalPreparedStatementSetter implements Setter<PreparedStatement, BigDecimal> {
    private final int columnIndex;

    public BigDecimalPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, BigDecimal value) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.NUMERIC);
        } else {
            target.setBigDecimal(columnIndex, value);
        }
    }
}
