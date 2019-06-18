package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;

public final class BigIntegerFromStringResultSetGetter implements
		Getter<ResultSet, BigInteger>,
		ContextualGetter<ResultSet, BigInteger>
{

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
	public BigInteger get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "BigIntegerFromStringResultSetGetter{" +
                "getter=" + getter +
                '}';
    }
}
