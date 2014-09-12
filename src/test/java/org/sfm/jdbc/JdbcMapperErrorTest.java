package org.sfm.jdbc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.beans.Foo;
import org.sfm.jdbc.mockdb.MockResultSet;
import org.sfm.map.FieldMapper;
import org.sfm.map.InstantiationMappingException;
import org.sfm.map.LogFieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

public class JdbcMapperErrorTest {

	@Test
	public void testHandleMapperErrorSetterNotFound() throws NoSuchMethodException, SecurityException, IOException {
		ResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilderImpl<DbObject>(DbObject.class);
		MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);
		
		builder.mapperBuilderErrorHandler(errorHandler);
		
		builder.addMapping("notthere1", 1);
		
		verify(errorHandler).setterNotFound(DbObject.class, "notthere1");
		
		builder.addMapping("notthere2", "col");
		
		verify(errorHandler).setterNotFound(DbObject.class, "notthere2");
		
		builder.addIndexedColumn("notthere3");
		
		verify(errorHandler).setterNotFound(DbObject.class, "notthere3");
		
		builder.addNamedColumn("notthere4");
		
		verify(errorHandler).setterNotFound(DbObject.class, "notthere4");
	}
	
	static class MyClass {
		public Foo prop;
	}
	@Test
	public void testHandleMapperErrorgGetterNotFound() throws NoSuchMethodException, SecurityException, IOException {
		ResultSetMapperBuilder<MyClass> builder = new ResultSetMapperBuilderImpl<MyClass>(MyClass.class);
		MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);
		
		builder.mapperBuilderErrorHandler(errorHandler);
		
		builder.addMapping("prop", 1);
		builder.addMapping("prop", "col");
		builder.addIndexedColumn("prop");
		builder.addNamedColumn("prop");
		
		verify(errorHandler).getterNotFound("No getter for column column:1 type class org.sfm.beans.Foo");
		verify(errorHandler).getterNotFound("No getter for column col type class org.sfm.beans.Foo");
		verify(errorHandler, times(2)).getterNotFound("No getter for column prop type class org.sfm.beans.Foo");
	}
	
	@Test
	public void setChangeFieldMapperErrorHandler() throws NoSuchMethodException, SecurityException, IOException {
		ResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilderImpl<DbObject>(DbObject.class);
		builder.fieldMapperErrorHandler(new LogFieldMapperErrorHandler());
		
		builder.addIndexedColumn("id");
		
		try  {
			builder.fieldMapperErrorHandler(new LogFieldMapperErrorHandler());
			fail("Expect exception");
		} catch(IllegalStateException e) {
			// expected
		}
	}
	
	@Test
	public void testInstantiatorError() {
		JdbcMapperImpl<DbObject> mapper = new JdbcMapperImpl<DbObject>(null,
				new Instantiator<ResultSet, DbObject>() {
					@Override
					public DbObject newInstance(ResultSet s) throws Exception {
						throw new UnsupportedOperationException();
					}
				}, new RethrowJdbcMapperErrorHandler());
		
		try {
			mapper.map(null);
			fail("Expecte error");
		} catch(InstantiationMappingException e) {}
	}
	
	
	@Test
	public void testInstantiatorHandlerError() throws MappingException, SQLException {
		
		MyJdbcMapperErrorHandler handler = new MyJdbcMapperErrorHandler();
		@SuppressWarnings("unchecked")
		FieldMapper<ResultSet, DbObject>[] fields = new FieldMapper[] {};
		JdbcMapperImpl<DbObject> mapper = new JdbcMapperImpl<DbObject>(fields,
				new Instantiator<ResultSet, DbObject>() {
					@Override
					public DbObject newInstance(ResultSet s) throws Exception {
						return new DbObject();
					}
				}, handler);
		final Error error = new Error();
		mapper.forEach(new MockResultSet(1), new RowHandler<DbObject>() {

			@Override
			public void handle(DbObject t) throws Exception {
				throw error;
			}
		});
		
		assertSame(error, handler.error);
		
		
	}
}
