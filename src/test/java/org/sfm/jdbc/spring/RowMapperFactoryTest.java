package org.sfm.jdbc.spring;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class RowMapperFactoryTest {

	@Test
	public void testRowMapperFactory() throws SQLException, ParseException  {
		RowMapper<DbObject> mapper = new RowMapperFactory().newMapper(DbObject.class);
		
		JdbcTemplate template = new JdbcTemplate(new SingleConnectionDataSource(DbHelper.objectDb(), true));
		
		List<DbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
		DbHelper.assertDbObjectMapping(results.get(0));
		
	}

}
