package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ClobResultSetGetter implements
		Getter<ResultSet, Clob>,
		ContextualGetter<ResultSet, Clob>
{
	private final int column;
	
	public ClobResultSetGetter(final int column) {
		this.column = column;
	}

	public Clob get(final ResultSet target) throws SQLException {
		return target.getClob(column);
	}

	@Override
	public Clob get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "ClobResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
