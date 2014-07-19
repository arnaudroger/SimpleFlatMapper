package org.flatmap.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.flatmap.reflect.Getter;
import org.flatmap.reflect.primitive.ByteGetter;

public class ByteNameResultSetGetter implements ByteGetter<ResultSet>, Getter<ResultSet, Byte> {

	private final String column;
	
	public ByteNameResultSetGetter(String column) {
		this.column = column;
	}

	@Override
	public byte getByte(ResultSet target) throws SQLException {
		return target.getByte(column);
	}

	@Override
	public Byte get(ResultSet target) throws Exception {
		return getByte(target);
	}
}
