package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public final class UndefinedDateResultSetGetter implements
		Getter<java.sql.ResultSet, Date>, ContextualGetter<ResultSet, Date> {
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
	public Date get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "UndefinedDateResultSetGetter{" +
                "property=" + column +
                '}';
    }

}
