package org.sfm.jdbc.getter;

import java.sql.ResultSet;

import org.sfm.reflect.Getter;

public final class ByteArrayResultSetGetter implements Getter<ResultSet, byte[]> {

	private final int column;
	
	public ByteArrayResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public byte[] get(final ResultSet target) throws Exception {
		return target.getBytes(column);
	}
}
