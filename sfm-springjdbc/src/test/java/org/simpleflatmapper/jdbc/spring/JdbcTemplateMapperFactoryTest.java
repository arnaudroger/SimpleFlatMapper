package org.simpleflatmapper.jdbc.spring;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.core.utils.ListCollectorHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class JdbcTemplateMapperFactoryTest {
	
	JdbcTemplate template;
	
	@Before
	public void setUp() throws SQLException {
		template = new JdbcTemplate(new SingleConnectionDataSource(DbHelper.objectDb(), true));
	}

	@Test
	public void testRowMapper() throws SQLException, ParseException  {
		RowMapper<DbObject> mapper = JdbcTemplateMapperFactory.newInstance().newRowMapper(DbObject.class);
		List<DbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
		DbHelper.assertDbObjectMapping(results.get(0));
	}
	
	@Test
	public void testPreparedStatementCallback() throws SQLException, ParseException  {
		PreparedStatementCallback<List<DbObject>> mapper = JdbcTemplateMapperFactory.newInstance().newPreparedStatementCallback(DbObject.class);
		List<DbObject> results = template.execute(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
		DbHelper.assertDbObjectMapping(results.get(0));
	}

	@Test
	public void testResultSetExtractor() throws SQLException, ParseException  {
		ResultSetExtractor<List<DbObject>> mapper = JdbcTemplateMapperFactory.newInstance().newResultSetExtractor(DbObject.class);
		List<DbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
		DbHelper.assertDbObjectMapping(results.get(0));
	}

	@Test
	public void testPreparedStatementCallbackWithHandler() throws SQLException, ParseException  {
		JdbcTemplateMapper<DbObject> mapper = JdbcTemplateMapperFactory.newInstance().newMapper(DbObject.class);
		List<DbObject> results = 
			template
				.execute(DbHelper.TEST_DB_OBJECT_QUERY, 
						mapper.newPreparedStatementCallback(new ListCollectorHandler<DbObject>())).getList();
		DbHelper.assertDbObjectMapping(results.get(0));
	}
	@Test
	public void testResultSetExtractorWithHandler() throws SQLException, ParseException  {
		JdbcTemplateMapper<DbObject> mapper = JdbcTemplateMapperFactory.newInstance().newMapper(DbObject.class);
		List<DbObject> results = 
			template
				.query(DbHelper.TEST_DB_OBJECT_QUERY, 
						mapper.newResultSetExtractor(new ListCollectorHandler<DbObject>())).getList();
		DbHelper.assertDbObjectMapping(results.get(0));
	}
}
