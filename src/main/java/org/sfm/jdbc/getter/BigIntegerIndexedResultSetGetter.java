package org.sfm.jdbc.getter;

import java.math.BigInteger;
import java.sql.ResultSet;

import org.sfm.reflect.Getter;

public final class BigIntegerIndexedResultSetGetter implements Getter<ResultSet, BigInteger> {

	private final int column;

	public BigIntegerIndexedResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public BigInteger get(final ResultSet target) throws Exception {
		return new BigInteger(target.getString(column));
	}
}
