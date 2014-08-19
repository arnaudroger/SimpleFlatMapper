package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.FloatGetter;

public final class FloatIndexedResultSetGetter implements FloatGetter<ResultSet>, Getter<ResultSet, Float> {

	private final int column;
	
	public FloatIndexedResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public float getFloat(final ResultSet target) throws SQLException {
		return target.getFloat(column);
	}

	@Override
	public Float get(final ResultSet target) throws Exception {
		final float f = getFloat(target);
		if (target.wasNull()) {
			return null;
		} else {
			return Float.valueOf(f);
		}
	}
}
