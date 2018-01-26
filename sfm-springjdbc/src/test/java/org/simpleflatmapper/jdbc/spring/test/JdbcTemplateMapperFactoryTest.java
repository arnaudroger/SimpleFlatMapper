package org.simpleflatmapper.jdbc.spring.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.jdbc.spring.PreparedStatementCallbackImpl;
import org.simpleflatmapper.jdbc.spring.ResultSetExtractorImpl;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.ListCollector;
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
		PreparedStatementCallbackImpl<DbObject> mapper = JdbcTemplateMapperFactory.newInstance().newPreparedStatementCallback(DbObject.class);
		List<DbObject> results = 
			template
				.execute(DbHelper.TEST_DB_OBJECT_QUERY, 
						mapper.newPreparedStatementCallback(new ListCollector<DbObject>())).getList();
		DbHelper.assertDbObjectMapping(results.get(0));
	}
	@Test
	public void testResultSetExtractorWithHandler() throws SQLException, ParseException  {
		ResultSetExtractorImpl<DbObject> mapper = JdbcTemplateMapperFactory.newInstance().newResultSetExtractor(DbObject.class);
		List<DbObject> results = 
			template
				.query(DbHelper.TEST_DB_OBJECT_QUERY, 
						mapper.newResultSetExtractor(new ListCollector<DbObject>())).getList();
		DbHelper.assertDbObjectMapping(results.get(0));
	}



	@Test
	public void testIssue483IgnorePropertyNotFound()  {
		ResultSetExtractor<List<ThinDbObject>> mapper = 
				JdbcTemplateMapperFactory.newInstance().ignorePropertyNotFound().newResultSetExtractor(ThinDbObject.class);
		List<ThinDbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
		Assert.assertEquals(1, results.get(0).getId());
		Assert.assertEquals("name 1", results.get(0).getName());
	}
	
	public static class ThinDbObject {
		private long id;
		private String name;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
