package org.sfm.jdbc;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbFinalObject;
import org.sfm.utils.Handler;
import org.sfm.utils.ListHandler;

public class JdbcMapperFactoryTest {

	JdbcMapperFactory asmFactory = JdbcMapperFactory.newInstance().useAsm(true);
	JdbcMapperFactory nonAsmFactory = JdbcMapperFactory.newInstance().useAsm(false);
	
	@Test
	public void testAsmDbObjectMappingFromDbWithMetaData()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				JdbcMapper<DbObject> mapper = asmFactory.newMapper(DbObject.class, rs.getMetaData());
				assertMapPsDbObject(rs, mapper);
			}
		});
	}
	
	@Test
	public void testNonAsmDbObjectMappingFromDbWithMetaData()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				JdbcMapper<DbObject> mapper = nonAsmFactory.newMapper(DbObject.class, rs.getMetaData());
				assertMapPsDbObject(rs, mapper);
			}
		});
	}
	
	@Test
	public void testAsmDbObjectMappingFromDbDynamic()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				JdbcMapper<DbObject> mapper = asmFactory.newMapper(DbObject.class);
				assertMapPsDbObject(ps.executeQuery(), mapper);
			}
		});
	}
	
	@Test
	public void testNonAsmDbObjectMappingFromDbDynamic()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				JdbcMapper<DbObject> mapper = nonAsmFactory.newMapper(DbObject.class);
				assertMapPsDbObject(ps.executeQuery(), mapper);
			}
		});
	}
	
	@Test
	public void testAsmFinalDbObjectMappingFromDbDynamic()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				JdbcMapper<DbFinalObject> mapper = asmFactory.newMapper(DbFinalObject.class);
				assertMapPsFinalDbObject(ps.executeQuery(), mapper);
			}
		});
	}
	
	@Test
	public void testNonAsmFinalDbObjectMappingFromDbDynamic()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				JdbcMapper<DbFinalObject> mapper = nonAsmFactory.newMapper(DbFinalObject.class);
				assertMapPsFinalDbObject(ps.executeQuery(), mapper);
			}
		});
	}	
	
	private void assertMapPsDbObject(ResultSet rs,
			JdbcMapper<DbObject> mapper) throws Exception,
			ParseException {
		List<DbObject> list = mapper.forEach(rs, new ListHandler<DbObject>()).getList();
		assertEquals(1,  list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	
	
	private void assertMapPsFinalDbObject(ResultSet rs,
			JdbcMapper<DbFinalObject> mapper) throws Exception,
			ParseException {
		List<DbFinalObject> list = mapper.forEach(rs, new ListHandler<DbFinalObject>()).getList();
		assertEquals(1,  list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
}
