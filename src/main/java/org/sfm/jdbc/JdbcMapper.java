package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.map.Mapper;
import org.sfm.map.MappingException;
import org.sfm.utils.RowHandler;

public interface JdbcMapper<T> extends Mapper<ResultSet, T> {
	
	/**
	 * Loop over the resultSet, map each row to a new instance of T and call back the handler
	 * @param rs the resultSet
	 * @param handle the handler that will get the callback
	 * @return the handler passed in
	 * @throws SQLException if sql error occurs
	 * @throws MappingException if an error occurs during the mapping
	 */
	<H extends RowHandler<T>> H forEach(ResultSet rs, H handle) throws SQLException, MappingException;
	
}
