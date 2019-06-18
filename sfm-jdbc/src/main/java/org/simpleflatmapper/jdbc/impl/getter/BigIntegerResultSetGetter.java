package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;

public final class BigIntegerResultSetGetter implements
		Getter<ResultSet, BigInteger>,
		ContextualGetter<ResultSet, BigInteger>
{

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
	public BigInteger get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "BigIntegerResultSetGetter{" +
                "property=" + column +
                '}';
    }


}
