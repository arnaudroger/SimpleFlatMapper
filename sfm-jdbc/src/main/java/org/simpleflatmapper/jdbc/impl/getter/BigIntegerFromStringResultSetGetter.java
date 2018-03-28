package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;

public final class BigIntegerFromStringResultSetGetter implements Getter<ResultSet, BigInteger> {

	private final Getter<ResultSet, String> getter;

	public BigIntegerFromStringResultSetGetter(final Getter<ResultSet, String> getter) {
		this.getter = getter;
	}

	@Override
	public BigInteger get(final ResultSet target) throws Exception {
		String val = getter.get(target);
		if (val == null) return null;
		return new BigInteger(val);
	}

    @Override
    public String toString() {
        return "BigIntegerFromStringResultSetGetter{" +
                "getter=" + getter +
                '}';
    }
}
