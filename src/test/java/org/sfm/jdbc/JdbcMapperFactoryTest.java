package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbObjectWithAlias;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.FieldMapper;
import org.sfm.tuples.Tuples;
import org.sfm.utils.ListHandler;
import org.sfm.utils.RowHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class JdbcMapperFactoryTest {

	JdbcMapperFactory asmFactory = JdbcMapperFactory.newInstance().useAsm(true);
	JdbcMapperFactory nonAsmFactory = JdbcMapperFactory.newInstance().useAsm(false);


	@Test
	public void testFactoryOnTuples() {
		assertNotNull(JdbcMapperFactory.newInstance().newMapper(Tuples.typeDef(Date.class, Date.class)));
		assertNotNull(JdbcMapperFactory.newInstance().newBuilder(Tuples.typeDef(Date.class, Date.class)));
	}

	@Test
	public void testAsmDbObjectMappingFromDbWithMetaData()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				JdbcMapper<DbObject> mapper = asmFactory.newMapper(DbObject.class, rs.getMetaData());
				assertMapPsDbObject(rs, mapper);
			}
		});
	}
	
	@Test
	public void testAsmDbObjectWithAliasMappingFromDbWithMetaData()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				JdbcMapper<DbObjectWithAlias> mapper = asmFactory.newMapper(DbObjectWithAlias.class, rs.getMetaData());
				assertMapPsDbObjectWithAlias(rs, mapper);
			}
		});
	}
	
	@Test
	public void testNonAsmDbObjectMappingFromDbWithMetaData()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
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
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
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
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
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
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
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
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				JdbcMapper<DbFinalObject> mapper = nonAsmFactory.newMapper(DbFinalObject.class);
				assertMapPsFinalDbObject(ps.executeQuery(), mapper);
			}
		});
	}	
	
	@Test
	public void testFieldErrorHandling()
			throws SQLException, Exception, ParseException {
		@SuppressWarnings("unchecked")
		FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler  = mock(FieldMapperErrorHandler.class);
		final Exception exception = new Exception("Error!");
		JdbcMapper<DbObject> mapper = JdbcMapperFactory.newInstance()
			.fieldMapperErrorHandler(fieldMapperErrorHandler)
			.addCustomFieldMapper("id",  new FieldMapper<ResultSet, DbObject>() {
				@Override
				public void map(ResultSet source, DbObject target) throws Exception {
					throw exception;
				}
			}).newBuilder(DbObject.class).addMapping("id").mapper();
		
		List<DbObject> list = mapper.forEach(new MockDbObjectResultSet(1), new ListHandler<DbObject>()).getList();
		assertNotNull(list.get(0));
		verify(fieldMapperErrorHandler).errorMappingField(eq(new JdbcColumnKey("id", 1)), any(), same(list.get(0)), same(exception));
	}
	
	
	@Test
	public void testFieldErrorHandlingOnResultSet()
			throws SQLException, Exception, ParseException {
		@SuppressWarnings("unchecked")
		FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler  = mock(FieldMapperErrorHandler.class);
		ResultSet rs = mock(ResultSet.class);
		
		final Exception exception = new SQLException("Error!");
		JdbcMapper<DbObject> mapper = JdbcMapperFactory.newInstance()
			.fieldMapperErrorHandler(fieldMapperErrorHandler)
			.newBuilder(DbObject.class).addMapping("id").mapper();
		
		when(rs.next()).thenReturn(true, false);
		when(rs.getLong(1)).thenThrow(exception);
		
		List<DbObject> list = mapper.forEach(rs, new ListHandler<DbObject>()).getList();
		assertNotNull(list.get(0));
		verify(fieldMapperErrorHandler).errorMappingField(eq(new JdbcColumnKey("id", 1)), any(), same(list.get(0)), same(exception));

	}

	@Test
	public void testSetRowHandlerError() throws SQLException {
		RowHandlerErrorHandler errorHandler = mock(RowHandlerErrorHandler.class);
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true, true, false);
		when(rs.getLong(1)).thenReturn(1l);

		final Exception exception = new SQLException("Error!");
		JdbcMapper<DbObject> mapper = JdbcMapperFactory.newInstance()
				.rowHandlerErrorHandler(errorHandler)
				.newBuilder(DbObject.class).addMapping("id").mapper();

		mapper.forEach(rs, new RowHandler<DbObject>() {
			@Override
			public void handle(DbObject dbObject) throws Exception {
				throw exception;
			}
		});
		verify(errorHandler, times(2)).handlerError(same(exception), any(DbObject.class));

	}
	
	private void assertMapPsDbObject(ResultSet rs,
			JdbcMapper<DbObject> mapper) throws Exception,
			ParseException {
		List<DbObject> list = mapper.forEach(rs, new ListHandler<DbObject>()).getList();
		assertEquals(1,  list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	
	private void assertMapPsDbObjectWithAlias(ResultSet rs,
			JdbcMapper<DbObjectWithAlias> mapper) throws Exception,
			ParseException {
		List<DbObjectWithAlias> list = mapper.forEach(rs, new ListHandler<DbObjectWithAlias>()).getList();
		assertEquals(1,  list.size());
		DbHelper.assertDbObjectWithAliasMapping(list.get(0));
	}
	
	private void assertMapPsFinalDbObject(ResultSet rs,
			JdbcMapper<DbFinalObject> mapper) throws Exception,
			ParseException {
		List<DbFinalObject> list = mapper.forEach(rs, new ListHandler<DbFinalObject>()).getList();
		assertEquals(1,  list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
}
