package org.flatmap.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.flatmap.reflect.Getter;

public class TimestampNameResultSetGetter implements Getter<ResultSet, Timestamp> {
	private final String name;
	
	public TimestampNameResultSetGetter(String name) {
		this.name = name;
	}

	public Timestamp get(ResultSet target) throws SQLException {
		return target.getTimestamp(name);
	}

}
