package org.sfm.jdbc;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.utils.Handler;

public class JdbcMapperFactoryTest {

	@Test
	public void testDbObjectMappingFromDbWithMetaData()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				JdbcMapper<DbObject> mapper = JdbcMapperFactory.newInstance().newMapper(DbObject.class, rs.getMetaData());
				assertMapPs(rs, mapper);
			}
		});
	}
	
	@Test
	public void testDbObjectMappingFromDbDynamic()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				JdbcMapper<DbObject> mapper = JdbcMapperFactory.newInstance().newMapper(DbObject.class);
				assertMapPs(ps, mapper);
			}
		});
	}
	private void assertMapPs(ResultSet rs,
			JdbcMapper<DbObject> mapper) throws Exception,
			ParseException {
		List<DbObject> list = mapper.list(rs);
		assertEquals(1,  list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	private void assertMapPs(PreparedStatement ps,
			JdbcMapper<DbObject> mapper) throws Exception,
			ParseException {
		List<DbObject> list = mapper.list(ps);
		assertEquals(1,  list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
}
