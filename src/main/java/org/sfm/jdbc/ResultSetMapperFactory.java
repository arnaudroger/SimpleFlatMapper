package org.sfm.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetMapperFactory {
	public static <T> ResultSetMapper<T> newMapper(Class<T> target, ResultSetMetaData metaData) throws SQLException, NoSuchMethodException, SecurityException {
		ResultSetMapperBuilder<T> builder = new ResultSetMapperBuilder<>(target);
		
		for(int i = 0; i < metaData.getColumnCount(); i++) {
			builder.addIndexedColumn(metaData.getColumnName(i +1));
		}
		
		return builder.mapper();
	}
}
