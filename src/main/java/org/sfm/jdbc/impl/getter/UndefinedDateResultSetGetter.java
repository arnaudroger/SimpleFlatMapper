package org.sfm.jdbc.impl.getter;

import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;

public class UndefinedDateResultSetGetter implements
		Getter<java.sql.ResultSet, Date> {
	private final int column;
	
	public UndefinedDateResultSetGetter(final int column) {
		this.column = column;
	}

	public Date get(final ResultSet target) throws SQLException {
		Object o = target.getObject(column);
		if (o == null) {
			return null;
			
		}
		if (o instanceof Date) {
			return (Date)o;
		} else if (o instanceof Number) {
			return new Date(((Number) o).longValue());
		}
		
		throw new SQLException("Expected date, cannot convert " + o  + " to date");
	}
}
