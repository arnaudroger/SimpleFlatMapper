package org.sfm.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

public final class MapperKey {

	public static MapperKey valueOf(final ResultSetMetaData metaData) throws SQLException {
		final String[] columns = new String[metaData.getColumnCount()];
		
		for(int i = 0; i < columns.length; i++) {
			columns[i] = metaData.getColumnName(i + 1);
		}
		
		return new MapperKey(columns);
	}
	
	private final String[] columns;

	public MapperKey(final String[] columns) {
		this.columns = columns;
	}

	@Override
	public boolean equals(final Object obj) {
		final String[] otherColumns = ((MapperKey)obj).columns;
        return Arrays.equals(columns, otherColumns);
	}
}
