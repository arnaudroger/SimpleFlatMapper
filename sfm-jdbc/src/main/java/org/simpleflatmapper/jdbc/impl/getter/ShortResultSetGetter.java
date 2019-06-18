package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ShortContextualGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ShortResultSetGetter implements
		ShortGetter<ResultSet>, Getter<ResultSet, Short>,
		ShortContextualGetter<ResultSet>, ContextualGetter<ResultSet, Short> {

	private final int column;
	
	public ShortResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public short getShort(final ResultSet target) throws SQLException {
		return target.getShort(column);
	}

	@Override
	public Short get(final ResultSet target) throws Exception {
		final short s = getShort(target);
		if (s == 0 && target.wasNull()) {
			return null;
		} else {
			return s;
		}
	}

	@Override
	public Short get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

	@Override
	public short getShort(ResultSet resultSet, Context mappingContext) throws Exception {
		return getShort(resultSet);
	}

    @Override
    public String toString() {
        return "ShortResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
