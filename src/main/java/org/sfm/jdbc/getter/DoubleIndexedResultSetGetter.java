package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;

public class DoubleIndexedResultSetGetter implements DoubleGetter<ResultSet>, Getter<ResultSet, Double> {

	private final int column;
	
	public DoubleIndexedResultSetGetter(int column) {
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
