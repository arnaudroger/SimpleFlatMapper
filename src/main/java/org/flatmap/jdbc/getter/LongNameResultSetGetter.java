package org.flatmap.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.flatmap.reflect.primitive.LongGetter;

public class LongNameResultSetGetter implements LongGetter<ResultSet> {

	private final String column;
	
	public LongNameResultSetGetter(String column) {
		this.column = column;
	}

	@Override
	public long getLong(ResultSet target) throws SQLException {
		return target.getLong(column);
	}
}
