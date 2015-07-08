package org.sfm.datastax;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import org.sfm.map.EnumarableMapper;
import org.sfm.map.Mapper;


/**
 * JdbcMapper will map from a {@link ResultSet} to an object of the specified type T
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
public interface DatastaxMapper<T> extends Mapper<Row, T>, EnumarableMapper<ResultSet, T, DriverException> {

}
