package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;

import java.math.BigInteger;
import java.sql.ResultSet;

public final class BigIntegerResultSetGetter implements Getter<ResultSet, BigInteger> {

	private final int column;

	public BigIntegerResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public BigInteger get(final ResultSet target) throws Exception {
		return new BigInteger(target.getString(column));
	}

    @Override
    public String toString() {
        return "BigIntegerResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
