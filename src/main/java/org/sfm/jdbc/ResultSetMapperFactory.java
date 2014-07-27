package org.sfm.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.sfm.reflect.InstantiatorFactory;

public class ResultSetMapperFactory {
	public static <T> JdbcMapper<T> newMapper(Class<T> target, ResultSetMetaData metaData) throws SQLException, NoSuchMethodException, SecurityException {
		ResultSetMapperBuilder<T> builder = new ResultSetMapperBuilder<>(target);
		
		for(int i = 0; i < metaData.getColumnCount(); i++) {
			builder.addIndexedColumn(metaData.getColumnName(i +1));
		}
		
		return new DelegateJdbcMapper<>(builder.mapper(), new InstantiatorFactory().getInstantiator(target));
	}
	
	public static <T> JdbcMapper<T> newMapper(Class<T> target) throws SQLException, NoSuchMethodException, SecurityException {
		return new DynamicJdbcMapper<T>(target, new InstantiatorFactory().getInstantiator(target));
	}
}
