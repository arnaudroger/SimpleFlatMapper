package org.sfm.jdbc.impl.getter;

import org.sfm.reflect.Getter;

import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class NReaderResultSetGetter implements Getter<ResultSet, Reader> {
	private final int column;
	
	public NReaderResultSetGetter(final int column) {
		this.column = column;
	}

	public Reader get(final ResultSet target) throws SQLException {
		return target.getNCharacterStream(column);
	}

    @Override
    public String toString() {
        return "NReaderResultSetGetter{" +
                "column=" + column +
                '}';
    }
}
