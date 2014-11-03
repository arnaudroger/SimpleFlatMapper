package org.sfm.jdbc.impl.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;

public final class NStringResultSetGetter implements Getter<ResultSet, String> {
	private final int column;
	
	public NStringResultSetGetter(final int column) {
		this.column = column;
	}

	public String get(final ResultSet target) throws SQLException {
		return target.getNString(column);
	}

}
