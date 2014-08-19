package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.FloatGetter;

public final class FloatNamedResultSetGetter implements FloatGetter<ResultSet>, Getter<ResultSet, Float> {

	private final String column;
	
	public FloatNamedResultSetGetter(final String column) {
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
