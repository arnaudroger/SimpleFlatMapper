package org.sfm.jdbc;

import java.sql.ResultSet;

import org.sfm.map.Mapper;
import org.sfm.utils.Handler;

public interface JdbcMapper<T> extends Mapper<ResultSet, T> {
	
	/**
	 * Loop over the resultSet, map each row to a new instance of T and call back the handler
	 * @param rs the resultSet
	 * @param handle the handler that will get the callback
	 * @return the handler passed in
	 * @throws Exception if anything bad occurs
	 */
	<H extends Handler<T>> H forEach(ResultSet rs, H handle) throws Exception;
	
}
