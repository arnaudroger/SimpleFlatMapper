package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.beans.Foo;
import org.sfm.jdbc.impl.JdbcMapperImpl;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.FieldMapper;
import org.sfm.map.impl.LogFieldMapperErrorHandler;
import org.sfm.map.impl.RethrowRowHandlerErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class JdbcMapperErrorTest {

	@Test
	public void testHandleMapperErrorSetterNotFound() throws NoSuchMethodException, SecurityException, IOException {
		MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().mapperBuilderErrorHandler(errorHandler).newBuilder(DbObject.class);

		builder.addMapping("notthere1", 1);
		
		verify(errorHandler).propertyNotFound(DbObject.class, "notthere1");
		
		
		builder.addMapping("notthere3");
		
		verify(errorHandler).propertyNotFound(DbObject.class, "notthere3");
		
	}
	
	static class MyClass {
		public Foo prop;
	}
	@Test
	public void testHandleMapperErrorGetterNotFound() throws NoSuchMethodException, SecurityException, IOException {
		MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);

		JdbcMapperBuilder<MyClass> builder = JdbcMapperFactoryHelper.asm().mapperBuilderErrorHandler(errorHandler).newBuilder(MyClass.class);

		builder.addMapping("prop", 1);
		
		builder.mapper();
		
		verify(errorHandler).getterNotFound("Could not find getter for ColumnKey [columnName=prop, columnIndex=1, sqlType=-99999] type class org.sfm.beans.Foo");
	}
	
	@Test
	public void setChangeFieldMapperErrorHandler() throws NoSuchMethodException, SecurityException, IOException {
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class);
		builder.addMapping("id");
		builder.fieldMapperErrorHandler(new LogFieldMapperErrorHandler<JdbcColumnKey>());
	}
	
	@Test
	public void testInstantiatorError() {
		JdbcMapperImpl<DbObject> mapper = new JdbcMapperImpl<DbObject>(null, null,
				new Instantiator<ResultSet, DbObject>() {
					@Override
					public DbObject newInstance(ResultSet s) throws Exception {
						throw new IOException();
					}
				}, new RethrowRowHandlerErrorHandler(), new JdbcMappingContextFactoryBuilder().newFactory());
		
		try {
			mapper.map(null);
			fail("Expected error");
		} catch(Exception e) {
            assertTrue(e instanceof IOException);
        }
	}
	
	
	@SuppressWarnings("unchecked")
    @Test
	public void testHandlerError() throws MappingException, SQLException {
		
		MyJdbcRawHandlerErrorHandler handler = new MyJdbcRawHandlerErrorHandler();
		@SuppressWarnings("unchecked")
		FieldMapper<ResultSet, DbObject>[] fields = new FieldMapper[] {};
		JdbcMapperImpl<DbObject> mapper = new JdbcMapperImpl<DbObject>(fields, new FieldMapper[] {},
                new Instantiator<ResultSet, DbObject>() {
					@Override
					public DbObject newInstance(ResultSet s) throws Exception {
						return new DbObject();
					}
				}, handler, new JdbcMappingContextFactoryBuilder().newFactory());
		final Error error = new Error();
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true, false);
		
		mapper.forEach(rs, new RowHandler<DbObject>() {

			@Override
			public void handle(DbObject t) throws Exception {
				throw error;
			}
		});
		
		assertSame(error, handler.error);
		
		
	}



}
