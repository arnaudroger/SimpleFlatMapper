package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.sfm.map.FieldMapper;

public interface ResultSetMapperBuilder<T> extends MapperBuilder<ResultSet, T, ColumnKey, JdbcMapper<T>,  ResultSetMapperBuilder<T>> {

	ResultSetMapperBuilder<T> addMapping(String property, String column, int sqlType);

	ResultSetMapperBuilder<T> addMapping(String property, String column);
	
	ResultSetMapperBuilder<T> addMapping(String property, int columnIndex, int sqlType);

	ResultSetMapperBuilder<T> addMapping(String property, int columnIndex);

	ResultSetMapperBuilder<T> addNamedColumn(String column, int sqlType);

	ResultSetMapperBuilder<T> addNamedColumn(String column);

	ResultSetMapperBuilder<T> addIndexedColumn(String column);

	ResultSetMapperBuilder<T> addIndexedColumn(String column, int index);
	
	ResultSetMapperBuilder<T> addIndexedColumn(String column, int index, int sqlType);

	ResultSetMapperBuilder<T> addMapper(FieldMapper<ResultSet, T> mapper);

	ResultSetMapperBuilder<T> addMapping(ResultSetMetaData metaData) throws SQLException;

}