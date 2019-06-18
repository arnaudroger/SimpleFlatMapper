package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class NClobResultSetGetter implements Getter<ResultSet, NClob>, ContextualGetter<ResultSet, NClob> {
	private final int column;
	
	public NClobResultSetGetter(final int column) {
		this.column = column;
	}

	public NClob get(final ResultSet target) throws SQLException {
		return target.getNClob(column);
	}

	@Override
	public NClob get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "NClobResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
