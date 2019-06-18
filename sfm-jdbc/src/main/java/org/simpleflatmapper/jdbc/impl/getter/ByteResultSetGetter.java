package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ByteContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ByteResultSetGetter implements
		ByteGetter<ResultSet>, Getter<ResultSet, Byte>,
		ByteContextualGetter<ResultSet>, ContextualGetter<ResultSet, Byte>
{

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
	public byte getByte(ResultSet resultSet, Context mappingContext) throws Exception {
		return getByte(resultSet);
	}

	@Override
	public Byte get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "ByteResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
