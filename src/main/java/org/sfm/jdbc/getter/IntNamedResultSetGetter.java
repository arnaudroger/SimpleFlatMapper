package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

public final class IntNamedResultSetGetter implements IntGetter<ResultSet>, Getter<ResultSet, Integer> {

	private final String column;
	
	public IntNamedResultSetGetter(final String column) {
		this.column = column;
	}

	@Override
	public int getInt(final ResultSet target) throws SQLException {
		return target.getInt(column);
	}

	@Override
	public Integer get(final ResultSet target) throws Exception {
		final int i = getInt(target);
		if (target.wasNull()) {
			return null;
		} else {
			return Integer.valueOf(i);
		}
	}
}
