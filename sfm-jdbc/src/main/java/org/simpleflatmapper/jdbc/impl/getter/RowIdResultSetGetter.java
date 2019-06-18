package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;

public final class RowIdResultSetGetter implements Getter<ResultSet, RowId>, ContextualGetter<ResultSet, RowId> {
	private final int column;
	
	public RowIdResultSetGetter(final int column) {
		this.column = column;
	}

	public RowId get(final ResultSet target) throws SQLException {
		return target.getRowId(column);
	}

	@Override
	public RowId get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "RowIdResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
