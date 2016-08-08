package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;

import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class NClobResultSetGetter implements Getter<ResultSet, NClob> {
	private final int column;
	
	public NClobResultSetGetter(final int column) {
		this.column = column;
	}

	public NClob get(final ResultSet target) throws SQLException {
		return target.getNClob(column);
	}

    @Override
    public String toString() {
        return "NClobResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
