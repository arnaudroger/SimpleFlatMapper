package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BigIntegerPreparedStatementIndexSetter implements PreparedStatementIndexSetter<BigInteger> {
    @Override
    public void set(PreparedStatement target, BigInteger value, int columnIndex, Context context) throws SQLException {
        if (value == null) {
            target.setNull(columnIndex, Types.NUMERIC);
        } else {
            target.setBigDecimal(columnIndex, new BigDecimal(value));
        }
    }
}
