package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public final class TimeResultSetGetter implements Getter<ResultSet, Time>, ContextualGetter<ResultSet, Time> {
	private final int column;
	
	public TimeResultSetGetter(final int column) {
		this.column = column;
	}

	public Time get(final ResultSet target) throws SQLException {
		return target.getTime(column);
	}

	@Override
	public Time get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "TimeResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
