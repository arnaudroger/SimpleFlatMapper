package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.DoubleContextualGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class DoubleResultSetGetter implements
		DoubleGetter<ResultSet>, Getter<ResultSet, Double>,
		DoubleContextualGetter<ResultSet>, ContextualGetter<ResultSet, Double>
{

	private final int column;
	
	public DoubleResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public double getDouble(final ResultSet target) throws SQLException {
		return target.getDouble(column);
	}

	@Override
	public Double get(final ResultSet target) throws Exception {
		final double d = getDouble(target);
		if (d == 0d && target.wasNull()) {
			return null;
		} else {
			return d;
		}
	}

	@Override
	public Double get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

	@Override
	public double getDouble(ResultSet resultSet, Context mappingContext) throws Exception {
		return getDouble(resultSet);
	}

    @Override
    public String toString() {
        return "DoubleResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
