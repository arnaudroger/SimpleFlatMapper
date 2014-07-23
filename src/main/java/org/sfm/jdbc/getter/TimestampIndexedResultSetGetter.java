package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.sfm.reflect.Getter;

public class TimestampIndexedResultSetGetter implements Getter<ResultSet, Timestamp> {
	private final int column;
	
	public TimestampIndexedResultSetGetter(int column) {
		this.column = column;
	}

	public Timestamp get(ResultSet target) throws SQLException {
		return target.getTimestamp(column);
	}

}
