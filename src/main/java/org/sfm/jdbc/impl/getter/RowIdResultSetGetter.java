package org.sfm.jdbc.impl.getter;

import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;

public final class RowIdResultSetGetter implements Getter<ResultSet, RowId> {
	private final int column;
	
	public RowIdResultSetGetter(final int column) {
		this.column = column;
	}

	public RowId get(final ResultSet target) throws SQLException {
		return target.getRowId(column);
	}
}
