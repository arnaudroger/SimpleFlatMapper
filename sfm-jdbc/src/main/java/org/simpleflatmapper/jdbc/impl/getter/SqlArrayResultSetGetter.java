package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SqlArrayResultSetGetter implements Getter<ResultSet, Array> {
	private final int column;
	
	public SqlArrayResultSetGetter(final int column) {
		this.column = column;
	}

	public Array get(final ResultSet target) throws SQLException {
		return target.getArray(column);
	}

    @Override
    public String toString() {
        return "SqlArrayResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
