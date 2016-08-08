package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public final class TimestampResultSetGetter implements Getter<ResultSet, Timestamp> {
	private final int column;
	
	public TimestampResultSetGetter(final int column) {
		this.column = column;
	}

	public Timestamp get(final ResultSet target) throws SQLException {
		return target.getTimestamp(column);
	}

    @Override
    public String toString() {
        return "TimestampResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
