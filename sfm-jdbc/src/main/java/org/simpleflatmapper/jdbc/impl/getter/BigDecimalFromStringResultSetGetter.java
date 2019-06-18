package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;

public final class BigDecimalFromStringResultSetGetter implements
		Getter<ResultSet, BigDecimal>,
		ContextualGetter<ResultSet, BigDecimal>
{

	private final Getter<ResultSet, String> getter;

	public BigDecimalFromStringResultSetGetter(final Getter<ResultSet, String> getter) {
		this.getter = getter;
	}

	@Override
	public BigDecimal get(final ResultSet target) throws Exception {
		String val = getter.get(target);
		if (val == null) return null;
		return new BigDecimal(val);
	}

	@Override
	public BigDecimal get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "BigDecimalFromStringResultSetGetter{" +
                "getter=" + getter +
                '}';
    }
}
