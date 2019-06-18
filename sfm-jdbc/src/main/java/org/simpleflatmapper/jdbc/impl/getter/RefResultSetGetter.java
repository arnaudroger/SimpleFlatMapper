package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class RefResultSetGetter implements Getter<ResultSet, Ref>, ContextualGetter<ResultSet, Ref> {
	private final int column;
	
	public RefResultSetGetter(final int column) {
		this.column = column;
	}

	public Ref get(final ResultSet target) throws SQLException {
		return target.getRef(column);
	}

	@Override
	public Ref get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

	@Override
    public String toString() {
        return "RefResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
