package org.sfm.jdbc.impl.getter;

import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;

public final class NClobResultSetGetter implements Getter<ResultSet, NClob> {
	private final int column;
	
	public NClobResultSetGetter(final int column) {
		this.column = column;
	}

	public NClob get(final ResultSet target) throws SQLException {
		return target.getNClob(column);
	}

}
