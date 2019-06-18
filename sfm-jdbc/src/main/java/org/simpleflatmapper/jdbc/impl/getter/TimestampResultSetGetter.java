package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public final class TimestampResultSetGetter implements Getter<ResultSet, Timestamp>, ContextualGetter<ResultSet, Timestamp> {
	private final int column;
	
	public TimestampResultSetGetter(final int column) {
		this.column = column;
	}

	public Timestamp get(final ResultSet target) throws SQLException {
		return target.getTimestamp(column);
	}

	@Override
	public Timestamp get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

	@Override
    public String toString() {
        return "TimestampResultSetGetter{" +
                "property=" + column +
                '}';
    }

}
