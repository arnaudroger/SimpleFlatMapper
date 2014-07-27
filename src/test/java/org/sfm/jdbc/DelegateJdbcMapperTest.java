package org.sfm.jdbc;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.Handler;
import org.sfm.utils.ListHandler;

public class DelegateJdbcMapperTest {
	
	final JdbcMapper<DbObject> mapper;
	
	public DelegateJdbcMapperTest() throws NoSuchMethodException, SecurityException {
		mapper = new DelegateJdbcMapper<>(ResultSetMapperBuilderTest.newManualMapper(), 
				 new Instantiator<DbObject>() {
			@Override
			public DbObject newInstance() throws Exception {
				return new DbObject();
			}
		});
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
