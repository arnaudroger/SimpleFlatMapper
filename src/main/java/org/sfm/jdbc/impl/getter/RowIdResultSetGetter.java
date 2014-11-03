package org.sfm.jdbc.impl.getter;

import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;

import org.sfm.reflect.Getter;

public final class RowIdResultSetGetter implements Getter<ResultSet, RowId> {
	private final int column;
	
	public RowIdResultSetGetter(final int column) {
		this.column = column;
	}

	public RowId get(final ResultSet target) throws SQLException {
		return target.getRowId(column);
	}
}
