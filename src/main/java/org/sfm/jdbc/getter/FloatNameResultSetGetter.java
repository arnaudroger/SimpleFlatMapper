package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.FloatGetter;

public class FloatNameResultSetGetter implements FloatGetter<ResultSet>, Getter<ResultSet, Float> {

	private final String column;
	
	public FloatNameResultSetGetter(String column) {
		this.column = column;
	}

	@Override
	public float getFloat(ResultSet target) throws SQLException {
		return target.getFloat(column);
	}

	@Override
	public Float get(ResultSet target) throws Exception {
		return getFloat(target);
	}
}
