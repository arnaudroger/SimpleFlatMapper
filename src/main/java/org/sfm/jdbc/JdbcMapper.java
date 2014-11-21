package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
/*IFJAVA8_START
import java.util.stream.Stream;
IFJAVA8_END*/
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
	
	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 * @throws MappingException
	 */
	Iterator<T> iterate(ResultSet rs) throws SQLException, MappingException;
	
	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 * @throws MappingException
	 */
	/*IFJAVA8_START
	Stream<T> stream(ResultSet rs) throws SQLException, MappingException;
	IFJAVA8_END*/

}
