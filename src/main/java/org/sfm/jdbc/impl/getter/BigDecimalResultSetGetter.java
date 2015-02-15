package org.sfm.jdbc.impl.getter;

import org.sfm.reflect.Getter;

import java.math.BigDecimal;
import java.sql.ResultSet;

public final class BigDecimalResultSetGetter implements Getter<ResultSet, BigDecimal> {

	private final int column;

	public BigDecimalResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public BigDecimal get(final ResultSet target) throws Exception {
		return target.getBigDecimal(column);
	}

    @Override
    public String toString() {
        return "BigDecimalResultSetGetter{" +
                "column=" + column +
                '}';
    }
}
