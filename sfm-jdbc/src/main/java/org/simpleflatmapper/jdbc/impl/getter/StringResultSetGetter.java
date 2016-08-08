package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class StringResultSetGetter implements Getter<ResultSet, String> {
	private final int column;
	
	public StringResultSetGetter(final int column) {
		this.column = column;
	}

	public String get(final ResultSet target) throws SQLException {
		return target.getString(column);
	}

    @Override
    public String toString() {
        return "StringResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
