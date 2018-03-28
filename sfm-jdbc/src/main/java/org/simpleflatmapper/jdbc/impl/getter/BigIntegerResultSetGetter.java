package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;

public final class BigIntegerResultSetGetter implements Getter<ResultSet, BigInteger> {

	private final int column;

	public BigIntegerResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public BigInteger get(final ResultSet target) throws Exception {
		BigDecimal val = target.getBigDecimal(column);
		if (val == null) return null;
		return val.toBigInteger();
	}

    @Override
    public String toString() {
        return "BigIntegerResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
