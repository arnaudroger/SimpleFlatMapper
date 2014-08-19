package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;

public final class StringNamedResultSetGetter implements Getter<ResultSet, String> {
	private final String name;
	
	public StringNamedResultSetGetter(final String name) {
		this.name = name;
	}

	public String get(final ResultSet target) throws SQLException {
		return target.getString(name);
	}

}
