package org.flatmap.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.flatmap.reflect.Getter;

public class IndexResultSetGetter implements Getter<ResultSet, Object> {
	private final int column;
	
	public IndexResultSetGetter(int column) {
		this.column = column;
	}

	public Object get(ResultSet target) throws SQLException {
		ResultSet rs = (ResultSet) target;
		return rs.getObject(column);
	}

}
