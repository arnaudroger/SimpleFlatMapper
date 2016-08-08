package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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

    @Override
    public String toString() {
        return "UndefinedDateResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
