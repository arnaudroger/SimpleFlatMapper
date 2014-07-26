package org.sfm.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetMapperFactory {
	public static <T> ResultSetMapper<T> newMapper(Class<T> target, ResultSetMetaData metaData) throws SQLException, NoSuchMethodException, SecurityException {
		ResultSetMapperBuilder<T> builder = new ResultSetMapperBuilder<>(target);
		
		for(int i = 0; i < metaData.getColumnCount(); i++) {
			builder.addColumn(metaData.getColumnName(i +1), i+1);
		}
		
		return builder.mapper();
	}
}
