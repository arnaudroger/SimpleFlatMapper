package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ShortGetter;

public class ShortIndexedResultSetGetter implements ShortGetter<ResultSet>, Getter<ResultSet, Short> {

	private final int column;
	
	public ShortIndexedResultSetGetter(int column) {
		this.column = column;
	}

	@Override
	public short getShort(ResultSet target) throws SQLException {
		return target.getShort(column);
	}

	@Override
	public Short get(ResultSet target) throws Exception {
		short s = getShort(target);
		if (target.wasNull()) {
			return null;
		} else {
			return Short.valueOf(s);
		}
	}
}
