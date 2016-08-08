package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.reflect.Getter;

import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ReaderResultSetGetter implements Getter<ResultSet, Reader> {
	private final int column;
	
	public ReaderResultSetGetter(final int column) {
		this.column = column;
	}

	public Reader get(final ResultSet target) throws SQLException {
		return target.getCharacterStream(column);
	}

    @Override
    public String toString() {
        return "ReaderResultSetGetter{" +
                "property=" + column +
                '}';
    }
}
