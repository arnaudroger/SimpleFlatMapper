package org.sfm.jdbc.impl.getter;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;

public final class ClobResultSetGetter implements Getter<ResultSet, Clob> {
	private final int column;
	
	public ClobResultSetGetter(final int column) {
		this.column = column;
	}

	public Clob get(final ResultSet target) throws SQLException {
		return target.getClob(column);
	}

}
