package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.core.utils.ListCollectorHandler;
import org.simpleflatmapper.core.utils.RowHandler;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Provide integration point with Spring JdbcTemplate.
 * <p>
 * It implements {@link org.springframework.jdbc.core.RowMapper}, {@link org.springframework.jdbc.core.PreparedStatementCallback} and {@link org.springframework.jdbc.core.ResultSetExtractor}.
 * Because some JdbcTemplate template signature match against a few of those type you might need to downcast, declare the variable with a specific type or use the type specific method in {@link JdbcTemplateMapperFactory}.
 *
 * <p>
 *
 * <code>
 * class MyDao {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;private final JdbcTemplateMapper&lt;DbObject&gt; jdbcMapper =<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JdbcTemplateMapperFactory.newInstance().newMapper(DbObject.class);<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;private final RowMapper&lt;DbObject&gt; rowMapper = jdbcMapper;<br>
 *<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public void doSomething() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;DbObject&gt; results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, rowMapper);<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 *<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public void doSomethingElse() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;template<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.query(TEST_DB_OBJECT_QUERY,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;jdbcMapper.newResultSetExtractor((o) -&gt; System.out.println(o.toString())));<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * }<br>
 *</code>
 *
 * @param <T> the mapped type
 * @see JdbcMapperFactory
 * @see JdbcMapper
 * @see JdbcTemplateMapperFactory#newPreparedStatementCallback(Class)
 * @see JdbcTemplateMapperFactory#newResultSetExtractor(Class)
 * @see JdbcTemplateMapperFactory#newRowMapper(Class)
 *
 */
public final class JdbcTemplateMapper<T> implements RowMapper<T>, PreparedStatementCallback<List<T>>, ResultSetExtractor<List<T>> {
	private final JdbcMapper<T> mapper;
	
	public JdbcTemplateMapper(JdbcMapper<T> mapper) {
		this.mapper = mapper;
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		return mapper.map(rs);
	}

	@Override
	public List<T> doInPreparedStatement(PreparedStatement ps) throws SQLException,
			DataAccessException {
		ResultSet rs = ps.executeQuery();
		try {
			return extractData(rs);
		} finally {
			rs.close();
		}
	}

    @Override
    public List<T> extractData(ResultSet rs) throws SQLException,
            DataAccessException {
        return mapper.forEach(rs, new ListCollectorHandler<T>()).getList();
    }
	
	public <H extends RowHandler<T>> PreparedStatementCallback<H> newPreparedStatementCallback(final H handler) {
		return new PreparedStatementCallback<H>() {
			@Override
			public H doInPreparedStatement(
					PreparedStatement ps)
					throws SQLException, DataAccessException {
				ResultSet rs = ps.executeQuery();
				ResultSetExtractor<H> resultSetExtractor = newResultSetExtractor(handler);
				try {
					return resultSetExtractor.extractData(rs);
				} finally {
					rs.close();
				}
			}
		};
	}

	public <H extends RowHandler<T>>  ResultSetExtractor<H> newResultSetExtractor(final H handler) {
		return new ResultSetExtractor<H>() {
			@Override
			public H extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				return mapper.forEach(rs, handler);
			}
		};
	}


}
