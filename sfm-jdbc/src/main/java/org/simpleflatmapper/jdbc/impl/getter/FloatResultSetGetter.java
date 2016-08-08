package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class FloatResultSetGetter implements FloatGetter<ResultSet>, Getter<ResultSet, Float> {

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
    public String toString() {
        return "FloatResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
