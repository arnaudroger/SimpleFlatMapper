package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SqlArrayResultSetGetter implements Getter<ResultSet, Array>, ContextualGetter<ResultSet, Array> {
	private final int column;
	
	public SqlArrayResultSetGetter(final int column) {
		this.column = column;
	}

	public Array get(final ResultSet target) throws SQLException {
		return target.getArray(column);
	}

	@Override
	public Array get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "SqlArrayResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
