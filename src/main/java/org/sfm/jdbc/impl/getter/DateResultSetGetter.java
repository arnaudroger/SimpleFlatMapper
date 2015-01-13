package org.sfm.jdbc.impl.getter;

import org.sfm.reflect.Getter;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DateResultSetGetter implements
		Getter<java.sql.ResultSet, Date> {
	private final int column;
	
	public DateResultSetGetter(final int column) {
		this.column = column;
	}

	public Date get(final ResultSet target) throws SQLException {
		return target.getDate(column);
	}
}
