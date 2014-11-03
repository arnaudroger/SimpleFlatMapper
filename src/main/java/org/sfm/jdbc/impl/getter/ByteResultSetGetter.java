package org.sfm.jdbc.impl.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ByteGetter;

public final class ByteResultSetGetter implements ByteGetter<ResultSet>, Getter<ResultSet, Byte> {

	private final int column;
	
	public ByteResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public byte getByte(final ResultSet target) throws SQLException {
		return target.getByte(column);
	}

	@Override
	public Byte get(final ResultSet target) throws Exception {
		final byte b = getByte(target);
		if (target.wasNull()) {
			return null;
		} else {
			return Byte.valueOf(b);
		}
	}
}
