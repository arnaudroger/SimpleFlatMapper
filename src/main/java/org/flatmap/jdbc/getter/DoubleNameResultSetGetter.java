package org.flatmap.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.flatmap.reflect.Getter;
import org.flatmap.reflect.primitive.DoubleGetter;

public class DoubleNameResultSetGetter implements DoubleGetter<ResultSet>, Getter<ResultSet, Double> {

	private final String column;
	
	public DoubleNameResultSetGetter(String column) {
		this.column = column;
	}

	@Override
	public double getDouble(ResultSet target) throws SQLException {
		return target.getDouble(column);
	}

	@Override
	public Double get(ResultSet target) throws Exception {
		return getDouble(target);
	}
}
