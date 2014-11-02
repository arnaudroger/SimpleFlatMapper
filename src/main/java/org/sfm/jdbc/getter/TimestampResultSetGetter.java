package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.sfm.reflect.Getter;

public final class TimestampResultSetGetter implements Getter<ResultSet, Timestamp> {
	private final int column;
	
	public TimestampResultSetGetter(final int column) {
		this.column = column;
	}

	public Timestamp get(final ResultSet target) throws SQLException {
		return target.getTimestamp(column);
	}

}
