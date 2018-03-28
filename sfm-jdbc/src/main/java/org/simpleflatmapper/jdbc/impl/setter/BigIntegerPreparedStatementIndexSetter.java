package org.simpleflatmapper.jdbc.impl.setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BigIntegerPreparedStatementIndexSetter implements PreparedStatementIndexSetter<BigInteger> {
    @Override
    public void set(PreparedStatement target, BigInteger value, int columnIndex) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.NUMERIC);
        } else {
            target.setBigDecimal(columnIndex, new BigDecimal(value));
        }
    }
}
