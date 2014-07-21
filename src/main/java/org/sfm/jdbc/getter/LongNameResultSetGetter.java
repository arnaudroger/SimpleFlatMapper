package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.LongGetter;

public class LongNameResultSetGetter implements LongGetter<ResultSet>, Getter<ResultSet, Long> {

	private final String column;
	
	public LongNameResultSetGetter(String column) {
		this.column = column;
	}

	@Override
	public long getLong(ResultSet target) throws SQLException {
		return target.getLong(column);
	}

	@Override
	public Long get(ResultSet target) throws Exception {
		return getLong(target);
	}
}
