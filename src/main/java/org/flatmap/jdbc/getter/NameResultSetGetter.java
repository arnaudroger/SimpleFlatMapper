package org.flatmap.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.flatmap.reflect.Getter;

public class NameResultSetGetter implements Getter<ResultSet, Object> {
	private final String name;
	
	public NameResultSetGetter(String name) {
		this.name = name;
	}

	public Object get(ResultSet target) throws SQLException {
		return target.getObject(name);
	}

}
