package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class BlobResultSetGetter implements Getter<ResultSet, Blob> {
	private final int column;
	
	public BlobResultSetGetter(final int column) {
		this.column = column;
	}

	public Blob get(final ResultSet target) throws SQLException {
		return target.getBlob(column);
	}

    @Override
    public String toString() {
        return "BlobResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
