package org.sfm.jdbc;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.utils.Handler;
import org.sfm.utils.ListHandler;

public class ResultSetMapperTest {
	
	final ResultSetMapper<DbObject> mapper;
	
	public ResultSetMapperTest() throws NoSuchMethodException, SecurityException {
		 mapper = ResultSetMapperBuilderTest.newManualMapper();
	}
	
	@Test
	public void testResultSetMapperForEachRS()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				List<DbObject> objects = mapper.forEach(ps.executeQuery(), new ListHandler<DbObject>()).getList();
				assertEquals(1, objects.size());
				DbHelper.assertDbObjectMapping(objects.get(0));
			}
		});
	}
	
	@Test
	public void testResultSetMapperForEachPS()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				List<DbObject> objects = mapper.forEach(ps, new ListHandler<DbObject>()).getList();
				assertEquals(1, objects.size());
				DbHelper.assertDbObjectMapping(objects.get(0));
			}
		});
	}
}
