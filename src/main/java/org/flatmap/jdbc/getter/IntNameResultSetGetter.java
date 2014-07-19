package org.flatmap.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.flatmap.reflect.Getter;
import org.flatmap.reflect.primitive.IntGetter;

public class IntNameResultSetGetter implements IntGetter<ResultSet>, Getter<ResultSet, Integer> {

	private final String column;
	
	public IntNameResultSetGetter(String column) {
		this.column = column;
	}

	@Override
	public int getInt(ResultSet target) throws SQLException {
		return target.getInt(column);
	}

	@Override
	public Integer get(ResultSet target) throws Exception {
		return getInt(target);
	}
}
