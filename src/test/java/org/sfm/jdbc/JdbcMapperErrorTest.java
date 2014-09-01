package org.sfm.jdbc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.beans.Foo;
import org.sfm.map.LogFieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;

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
	
}
