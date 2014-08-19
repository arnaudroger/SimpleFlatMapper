package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.BooleanGetter;

public final class BooleanNamedResultSetGetter implements BooleanGetter<ResultSet>, Getter<ResultSet, Boolean> {

	private final String column;
	
	public BooleanNamedResultSetGetter(final String column) {
		this.column = column;
	}

	@Override
	public boolean getBoolean(final ResultSet target) throws SQLException {
		return target.getBoolean(column);
	}

	@Override
	public Boolean get(final ResultSet target) throws Exception {
		final boolean b = getBoolean(target);
		if (target.wasNull()) {
			return null;
		} else {
			return Boolean.valueOf(b);
		}
	}
}
