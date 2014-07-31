package org.sfm.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.sfm.map.Mapper;
import org.sfm.utils.Handler;

public interface JdbcMapper<T> extends Mapper<ResultSet, T> {
	
	/**
	 * Loop over the resultSet, map each row to the t object and call back the handler
	 * @param rs the resultSet
	 * @param handle the handler that will get the callback
	 * @param t the object that will be mapped
	 * @return the handler passed in
	 * @throws Exception if anything bad occurs
	 */
	<H extends Handler<T>> H forEach(ResultSet rs, H handle, T t) throws Exception;
	
	/**
	 * Loop over the resultSet, map each row to a new instance of T and call back the handler
	 * @param rs the resultSet
	 * @param handle the handler that will get the callback
	 * @return the handler passed in
	 * @throws Exception if anything bad occurs
	 */
	<H extends Handler<T>> H forEach(ResultSet rs, H handle) throws Exception;
	
	/**
	 * Execute the ps and loop over the resultSet, map each row to a new instance of T and call back the handler, close the result set
	 * @param ps the preparedStatement
	 * @param handle the handler that will get the callback
	 * @return the handler passed in
	 * @throws Exception if anything bad occurs
	 */
	<H extends Handler<T>> H forEach(PreparedStatement ps, H handle) throws Exception;
	
	
	/**
	 * Return a list of new instance of T for each row.
	 * @param rs the result set
	 * @return the list of mapped T
	 * @throws Exception if anything bad occurs
	 */
	List<T> list(ResultSet rs) throws Exception;
	
	/**
	 * Return a list of new instance of T for each row.
	 * @param ps the PreparedStatement to execute
	 * @return the list of mapped T
	 * @throws Exception if anything bad occurs
	 */
	List<T> list(PreparedStatement ps) throws Exception;
}
