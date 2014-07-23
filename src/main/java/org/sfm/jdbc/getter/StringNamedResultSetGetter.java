package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;

public class StringNamedResultSetGetter implements Getter<ResultSet, String> {
	private final String name;
	
	public StringNamedResultSetGetter(String name) {
		this.name = name;
	}

	public String get(ResultSet target) throws SQLException {
		return target.getString(name);
	}

}
