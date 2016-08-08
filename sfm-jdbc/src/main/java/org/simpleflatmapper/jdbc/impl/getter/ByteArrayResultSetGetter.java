package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;

public final class ByteArrayResultSetGetter implements Getter<ResultSet, byte[]> {

	private final int column;
	
	public ByteArrayResultSetGetter(final int column) {
		this.column = column;
	}

	@Override
	public byte[] get(final ResultSet target) throws Exception {
		return target.getBytes(column);
	}

    @Override
    public String toString() {
        return "ByteArrayResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
