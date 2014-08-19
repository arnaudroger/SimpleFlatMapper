package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;

public final class DoubleNamedResultSetGetter implements DoubleGetter<ResultSet>, Getter<ResultSet, Double> {

	private final String column;
	
	public DoubleNamedResultSetGetter(final String column) {
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
