package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class BlobResultSetGetter implements
		Getter<ResultSet, Blob>,
		ContextualGetter<ResultSet, Blob>
{
	private final int column;
	
	public BlobResultSetGetter(final int column) {
		this.column = column;
	}

	public Blob get(final ResultSet target) throws SQLException {
		return target.getBlob(column);
	}

	@Override
	public Blob get(ResultSet resultSet, Context context) throws Exception {
		return get(resultSet);
	}

    @Override
    public String toString() {
        return "BlobResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
