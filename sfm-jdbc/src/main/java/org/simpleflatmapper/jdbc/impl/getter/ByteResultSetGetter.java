package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

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
		if (b == 0 && target.wasNull()) {
			return null;
		} else {
			return b;
		}
	}

    @Override
    public String toString() {
        return "ByteResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
