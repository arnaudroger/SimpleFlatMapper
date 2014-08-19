package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;

public final class StringIndexedResultSetGetter implements Getter<ResultSet, String> {
	private final int column;
	
	public StringIndexedResultSetGetter(final int column) {
		this.column = column;
	}

	public String get(final ResultSet target) throws SQLException {
		return target.getString(column);
	}

}
