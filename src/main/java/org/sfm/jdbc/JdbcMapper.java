package org.sfm.jdbc;

import org.sfm.map.EnumarableMapper;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingException;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END


/**
 * JdbcMapper will map from a {@link java.sql.ResultSet} to an object of the specified type T
 * <p>
 * JdbcMapper are instantiable via {@link org.sfm.jdbc.JdbcMapperFactory}.
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
 * @see org.sfm.jdbc.JdbcMapperFactory
 */
public interface JdbcMapper<T> extends Mapper<ResultSet, T>, EnumarableMapper<ResultSet, T, SQLException> {

	/**
	 *
	 * @param rs the result set
	 * @return a new mapping context valid for that resultSet
	 */
	MappingContext<ResultSet> newMappingContext(ResultSet rs);
}
