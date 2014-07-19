package org.flatmap.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.flatmap.reflect.Getter;

public class StringNameResultSetGetter implements Getter<ResultSet, String> {
	private final String name;
	
	public StringNameResultSetGetter(String name) {
		this.name = name;
	}

	public String get(ResultSet target) throws SQLException {
		return target.getString(name);
	}

}
