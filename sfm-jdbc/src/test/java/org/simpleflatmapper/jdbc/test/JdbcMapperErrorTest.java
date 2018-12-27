package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.Foo;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.error.LogFieldMapperErrorHandler;
import org.simpleflatmapper.map.mapper.MapperImpl;
import org.simpleflatmapper.reflect.Instantiator;

import java.io.IOException;
import java.sql.ResultSet;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class JdbcMapperErrorTest {

	@Test
	public void testHandleMapperErrorSetterNotFound() throws NoSuchMethodException, SecurityException, IOException {
		MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);
		JdbcMapperBuilder<DbObject> builder = JdbcMapperFactoryHelper.asm().mapperBuilderErrorHandler(errorHandler).newBuilder(DbObject.class);

		builder.addMapping("id", 1);
		builder.addMapping("notthere1", 2);
		
		verify(errorHandler).propertyNotFound(DbObject.class, "notthere1");
		
		
		builder.addMapping("notthere3");
		
		verify(errorHandler).propertyNotFound(DbObject.class, "notthere3");
		
	}
	
	public static class MyClass {
		public Foo prop;
	}
	@Test
	public void testHandleMapperErrorGetterNotFound() throws NoSuchMethodException, SecurityException, IOException {
		MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);

		JdbcMapperBuilder<MyClass> builder = JdbcMapperFactoryHelper.asm().mapperBuilderErrorHandler(errorHandler).newBuilder(MyClass.class);

		builder.addMapping("prop", 1);
		
		builder.mapper();
		
		verify(errorHandler).accessorNotFound("Could not find Getter for ColumnKey [columnName=prop, columnIndex=1, sqlType=-99999] returning type class org.simpleflatmapper.test.beans.Foo path prop. See https://github.com/arnaudroger/SimpleFlatMapper/wiki/Errors_CSFM_GETTER_NOT_FOUND");
	}
	
	@Test
	public void setChangeFieldMapperErrorHandler() throws NoSuchMethodException, SecurityException, IOException {
		JdbcMapperBuilder<DbObject> builder =
				JdbcMapperFactoryHelper
						.asm()
						.fieldMapperErrorHandler(new LogFieldMapperErrorHandler<JdbcColumnKey>())
						.newBuilder(DbObject.class);
		builder.addMapping("id");
	}
	
	@Test
	public void testInstantiatorError() {
		MapperImpl<ResultSet, DbObject> mapper = new MapperImpl<ResultSet, DbObject>(null, null,
				new BiInstantiator<ResultSet, MappingContext<? super ResultSet>, DbObject>() {
					@Override
					public DbObject newInstance(ResultSet s, MappingContext<? super ResultSet> context) throws Exception {
						throw new IOException();
					}
				});
		
		try {
			mapper.map(null, null);
			fail("Expected error");
		} catch(Exception e) {
            assertTrue(e instanceof IOException);
        }
	}
}
