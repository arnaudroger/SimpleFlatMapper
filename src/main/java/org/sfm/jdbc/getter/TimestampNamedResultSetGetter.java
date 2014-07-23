package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.sfm.reflect.Getter;

public class TimestampNamedResultSetGetter implements Getter<ResultSet, Timestamp> {
	private final String name;
	
	public TimestampNamedResultSetGetter(String name) {
		this.name = name;
	}

	public Timestamp get(ResultSet target) throws SQLException {
		return target.getTimestamp(name);
	}

}
