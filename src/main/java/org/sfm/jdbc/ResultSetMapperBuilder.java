package org.sfm.jdbc;

import java.sql.ResultSet;

import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuilderErrorHandler;

public interface ResultSetMapperBuilder<T> {


	ResultSetMapperBuilder<T> addMapping(String property, String column);

	ResultSetMapperBuilder<T> addMapping(String property, int column);
	

	ResultSetMapperBuilder<T> addNamedColumn(String column);

	ResultSetMapperBuilder<T> addIndexedColumn(String column);

	ResultSetMapperBuilder<T> addIndexedColumn(String column, int p);
	

	ResultSetMapperBuilder<T> fieldMapperErrorHandler(FieldMapperErrorHandler errorHandler);

	ResultSetMapperBuilder<T> mapperBuilderErrorHandler(MapperBuilderErrorHandler errorHandler);
	
	Mapper<ResultSet, T> mapper();

	Mapper<ResultSet, T>[] fields();

}