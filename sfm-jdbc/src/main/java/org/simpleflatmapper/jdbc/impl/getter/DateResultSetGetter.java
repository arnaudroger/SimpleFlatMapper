package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DateResultSetGetter implements
		Getter<java.sql.ResultSet, Date>,
		ContextualGetter<ResultSet, Date>
{
	private final int column;
	
	public DateResultSetGetter(final int column) {
		this.column = column;
	}

	public Date get(final ResultSet target) throws SQLException {
		return target.getDate(column);
	}

	@Override
	public Date get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "DateResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
