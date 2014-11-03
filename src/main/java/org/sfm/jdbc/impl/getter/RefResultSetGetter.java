package org.sfm.jdbc.impl.getter;

import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;

public final class RefResultSetGetter implements Getter<ResultSet, Ref> {
	private final int column;
	
	public RefResultSetGetter(final int column) {
		this.column = column;
	}

	public Ref get(final ResultSet target) throws SQLException {
		return target.getRef(column);
	}
}
