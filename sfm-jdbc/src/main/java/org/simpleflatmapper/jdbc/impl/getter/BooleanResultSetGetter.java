package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class BooleanResultSetGetter implements BooleanGetter<ResultSet>, Getter<ResultSet, Boolean> {

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
    public String toString() {
        return "BooleanResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
