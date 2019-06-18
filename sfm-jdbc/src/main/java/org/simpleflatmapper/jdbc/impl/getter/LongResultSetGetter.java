package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.LongContextualGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.LongGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class LongResultSetGetter implements
		LongGetter<ResultSet>,
		Getter<ResultSet, Long>,
		LongContextualGetter<ResultSet>,
		ContextualGetter<ResultSet, Long>
{

	private final int column;

	public LongResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public long getLong(final ResultSet target) throws SQLException {
		return target.getLong(column);
	}

	@Override
	public Long get(final ResultSet target) throws Exception {
		final long l = getLong(target);
		if (l == 0 && target.wasNull()) {
			return null;
		} else {
			return l;
		}
	}

	@Override
	public Long get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

	@Override
	public long getLong(ResultSet resultSet, Context mappingContext) throws Exception {
		return getLong(resultSet);
	}


	@Override
    public String toString() {
        return "LongResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
