package org.flatmap.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.flatmap.reflect.primitive.BooleanGetter;

public class BooleanNameResultSetGetter implements BooleanGetter<ResultSet> {

	private final String column;
	
	public BooleanNameResultSetGetter(String column) {
		this.column = column;
	}

	@Override
	public boolean getBoolean(ResultSet target) throws SQLException {
		return target.getBoolean(column);
	}
}
