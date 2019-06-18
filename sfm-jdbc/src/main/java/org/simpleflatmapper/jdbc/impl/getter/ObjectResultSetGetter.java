package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ObjectResultSetGetter implements Getter<ResultSet, Object>, ContextualGetter<ResultSet, Object> {
	private final int column;

	public ObjectResultSetGetter(final int column) {
		this.column = column;
	}

	public Object get(final ResultSet target) throws SQLException {
		return target.getObject(column);
	}

	@Override
	public Object get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "ObjectResultSetGetter{" +
                "property=" + column +
                '}';
    }

}
