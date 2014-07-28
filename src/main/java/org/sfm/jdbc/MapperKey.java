package org.sfm.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

public final class MapperKey {

	public static MapperKey valueOf(ResultSetMetaData metaData) throws SQLException {
		String[] columns = new String[metaData.getColumnCount()];
		
		for(int i = 0; i < columns.length; i++) {
			columns[i] = metaData.getColumnName(i + 1);
		}
		
		return new MapperKey(columns);
	}
	
	private final String[] columns;

	public MapperKey(String[] columns) {
		this.columns = columns;
	}

	@Override
	public boolean equals(Object obj) {
		String[] otherColumns = ((MapperKey)obj).columns;
        return Arrays.equals(columns, otherColumns);
	}
}
