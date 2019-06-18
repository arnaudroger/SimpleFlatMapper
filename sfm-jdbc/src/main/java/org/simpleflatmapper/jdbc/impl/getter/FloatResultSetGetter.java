package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.FloatContextualGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class FloatResultSetGetter implements
		FloatGetter<ResultSet>, Getter<ResultSet, Float>,
		FloatContextualGetter<ResultSet>, ContextualGetter<ResultSet, Float>
{

	private final int column;
	
	public FloatResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public float getFloat(final ResultSet target) throws SQLException {
		return target.getFloat(column);
	}

	@Override
	public Float get(final ResultSet target) throws Exception {
		final float f = getFloat(target);
		if (f == 0f && target.wasNull()) {
			return null;
		} else {
			return f;
		}
	}

	@Override
	public Float get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

	@Override
	public float getFloat(ResultSet resultSet, Context mappingContext) throws Exception {
		return getFloat(resultSet);
	}

    @Override
    public String toString() {
        return "FloatResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
