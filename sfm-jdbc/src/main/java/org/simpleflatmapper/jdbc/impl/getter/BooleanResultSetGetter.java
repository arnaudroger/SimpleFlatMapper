package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.BooleanContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class BooleanResultSetGetter implements
		BooleanGetter<ResultSet>, Getter<ResultSet, Boolean>,
		BooleanContextualGetter<ResultSet>, ContextualGetter<ResultSet, Boolean>
{

	private final int column;
	
	public BooleanResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public boolean getBoolean(final ResultSet target) throws SQLException {
		return target.getBoolean(column);
	}

	@Override
	public Boolean get(final ResultSet target) throws Exception {
		final boolean b = getBoolean(target);
		if (!b && target.wasNull()) {
			return null;
		} else {
			return b;
		}
	}

	@Override
	public boolean getBoolean(ResultSet resultSet, Context mappingContext) throws Exception {
		return getBoolean(resultSet);
	}

	@Override
	public Boolean get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "BooleanResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
