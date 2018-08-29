package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.context.MappingContextFactoryFromRows;
import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END


/**
 * JdbcMapper will map from a {@link java.sql.ResultSet} to an object of the specified type T
 * <p>
 * JdbcMapper are instantiable via {@link JdbcMapperFactory}.
 * <p>
 * <code>
 *     JdbcMapper&lt;MyClass&gt; jdbcMapper = JdbcMapperFactory.newInstance().newMapper(MyClass.class);<br>
 *         <br>
 *     ...<br>
 *         <br>
 *     try (ResultSet rs : ps.executeQuery()){<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;jdbcMapper.stream(rs).forEach(System.out::println);<br>
 *     }<br>
 * </code>
 *
 * @param <T> the type that the jdbcMapper is mapping to
 * @see JdbcMapperFactory
 */
public interface JdbcMapper<T> extends SetRowMapper<ResultSet, ResultSet, T, SQLException>, MappingContextFactoryFromRows<ResultSet, ResultSet, SQLException> 
		{

	/**
	 * map the current row of the ResultSet to a new newInstance of T.
	 * This method does not manage the iteration of the ResultSet and will only map the current row. No join aggregation will be performed.
	 * @param rs the resultSet
	 * @return a new mapped newInstance of T
	 * @throws MappingException if an exception occurs
	 */
	T map(ResultSet rs) throws MappingException;

	/**
	 * Loop over the resultSet, map each row to a new newInstance of T and call back the handler.
	 *<p>
	 * The method will return the handler passed as an argument so you can easily chain the calls like <br>
	 * <code>
	 *     List&lt;T&gt; list = jdbcMapper.forEach(rs, new ListHandler&lt;T&gt;()).getList();
	 * </code>
	 * <br>
	 *
	 * @param rs the resultSet
	 * @param handler the handler that will get the callback
	 * @param <H> the row handler type
	 * @return the handler passed in
	 * @throws SQLException if sql error occurs
	 * @throws MappingException if an error occurs during the mapping
	 *
	 */
	<H extends CheckedConsumer<? super T>> H forEach(final ResultSet rs, final H handler)
			throws SQLException, MappingException;

	/**
	 *
	 * @param rs the result set
	 * @return an iterator that will return a map object for each row of the result set.
	 * @throws SQLException if sql error occurs
	 * @throws MappingException if an error occurs during the mapping
	 */
	Iterator<T> iterator(ResultSet rs)
			throws SQLException, MappingException;
	/**
	 *
	 * @param rs the result set
	 * @return a stream that will contain a map object for each row of the result set.
	 * @throws SQLException if sql error occurs
	 * @throws MappingException if an error occurs during the mapping
	 */
	//IFJAVA8_START
	Stream<T> stream(ResultSet rs) throws SQLException, MappingException;
	//IFJAVA8_END
}
