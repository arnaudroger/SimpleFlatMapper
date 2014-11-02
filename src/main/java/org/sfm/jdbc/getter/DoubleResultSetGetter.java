package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;

public final class DoubleResultSetGetter implements DoubleGetter<ResultSet>, Getter<ResultSet, Double> {

	private final int column;
	
	public DoubleResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public double getDouble(final ResultSet target) throws SQLException {
		return target.getDouble(column);
	}

	@Override
	public Double get(final ResultSet target) throws Exception {
		final double d = getDouble(target);
		if (target.wasNull()) {
			return null;
		} else {
			return Double.valueOf(d);
		}
	}
}
