package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.IntContextualGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.IntGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class IntResultSetGetter implements
		IntGetter<ResultSet>, Getter<ResultSet, Integer>,
		IntContextualGetter<ResultSet>, ContextualGetter<ResultSet, Integer>
{

	private final int column;
	
	public IntResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public int getInt(final ResultSet target) throws SQLException {
		return target.getInt(column);
	}

	@Override
	public Integer get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

	@Override
	public int getInt(ResultSet resultSet, Context mappingContext) throws Exception {
		return getInt(resultSet);
	}

	@Override
	public Integer get(final ResultSet target) throws Exception {
		final int i = getInt(target);
		if (i == 0 && target.wasNull()) {
			return null;
		} else {
			return i;
		}
	}

    @Override
    public String toString() {
        return "IntResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
