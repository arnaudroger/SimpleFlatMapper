package org.sfm.jdbc.impl.getter;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;

public final class BlobResultSetGetter implements Getter<ResultSet, Blob> {
	private final int column;
	
	public BlobResultSetGetter(final int column) {
		this.column = column;
	}

	public Blob get(final ResultSet target) throws SQLException {
		return target.getBlob(column);
	}

}
