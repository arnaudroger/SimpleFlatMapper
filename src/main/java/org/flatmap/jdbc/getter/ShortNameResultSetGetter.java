package org.flatmap.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.flatmap.reflect.primitive.ShortGetter;

public class ShortNameResultSetGetter implements ShortGetter<ResultSet> {

	private final String column;
	
	public ShortNameResultSetGetter(String column) {
		this.column = column;
	}

	@Override
	public short getShort(ResultSet target) throws SQLException {
		return target.getShort(column);
	}
}
