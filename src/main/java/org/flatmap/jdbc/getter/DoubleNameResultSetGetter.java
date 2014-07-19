package org.flatmap.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.flatmap.reflect.primitive.DoubleGetter;

public class DoubleNameResultSetGetter implements DoubleGetter<ResultSet> {

	private final String column;
	
	public DoubleNameResultSetGetter(String column) {
		this.column = column;
	}

	@Override
	public double getDouble(ResultSet target) throws SQLException {
		return target.getDouble(column);
	}
}
