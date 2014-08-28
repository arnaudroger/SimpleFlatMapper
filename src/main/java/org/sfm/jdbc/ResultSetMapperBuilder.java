package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;

public interface ResultSetMapperBuilder<T> {

	ResultSetMapperBuilder<T> addMapping(String property, String column, int sqlType);

	ResultSetMapperBuilder<T> addMapping(String property, String column);
	
	ResultSetMapperBuilder<T> addMapping(String property, int columnIndex, int sqlType);

	ResultSetMapperBuilder<T> addMapping(String property, int columnIndex);

	ResultSetMapperBuilder<T> addNamedColumn(String column, int sqlType);

	ResultSetMapperBuilder<T> addNamedColumn(String column);

	ResultSetMapperBuilder<T> addIndexedColumn(String column);

	ResultSetMapperBuilder<T> addIndexedColumn(String column, int index);
	
	ResultSetMapperBuilder<T> addIndexedColumn(String column, int index, int sqlType);

	ResultSetMapperBuilder<T> addMapping(ResultSetMetaData metaData) throws SQLException;
	
	ResultSetMapperBuilder<T> fieldMapperErrorHandler(FieldMapperErrorHandler errorHandler);

	ResultSetMapperBuilder<T> mapperBuilderErrorHandler(MapperBuilderErrorHandler errorHandler);
	
	JdbcMapper<T> mapper() throws MapperBuildingException;

	FieldMapper<ResultSet, T>[] fields();

}