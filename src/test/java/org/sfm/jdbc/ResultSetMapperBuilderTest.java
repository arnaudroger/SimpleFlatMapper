package org.sfm.jdbc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import org.junit.Test;
import org.sfm.beans.DbBoxedPrimitveObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.beans.Foo;
import org.sfm.beans.PrimitiveObject;
import org.sfm.map.LogFieldMapperErrorHandler;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.Handler;

public class ResultSetMapperBuilderTest {

	@Test
	public void testSelectWithManualDefinition() throws Exception {
		JdbcMapper<DbObject> mapper = newManualMapper();
		testDbObjectMappingFromDb(mapper);
	}

	public static  JdbcMapper<DbObject> newManualMapper() throws NoSuchMethodException, SecurityException {
		ResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilderImpl<DbObject>(DbObject.class);
		
		builder.addMapping("id", "id");
		builder.addMapping("name", "name");
		builder.addMapping("email", "email");
		builder.addMapping("creationTime", "creation_time");
		builder.addMapping("typeOrdinal", "type_ordinal");
		builder.addMapping("typeName", "type_name");
		
		return builder.mapper();
	}
	
	@Test
	public void testSelectWithManualColumnDefinition() throws Exception {
		ResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilderImpl<DbObject>(DbObject.class);
		
		builder.addNamedColumn("id");
		builder.addNamedColumn("name");
		builder.addNamedColumn("email");
		builder.addNamedColumn("creation_time");
		builder.addNamedColumn("type_ordinal");
		builder.addNamedColumn("type_name");
		
		JdbcMapper<DbObject> mapper = builder.mapper();
		
		testDbObjectMappingFromDb(mapper);
	}
	
	@Test
	public void testNamedPrimitivesWithSetterAccess() throws Exception {
		ResultSetMapperBuilder<DbPrimitiveObjectWithSetter> builder = new ResultSetMapperBuilderImpl<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class);
		testNamedPrimitives(builder);
	}
	
	@Test
	public void testNamedBoxedPrimitives() throws Exception {
		ResultSetMapperBuilder<DbBoxedPrimitveObject> builder = new ResultSetMapperBuilderImpl<DbBoxedPrimitveObject>(DbBoxedPrimitveObject.class);
		testNamedPrimitives(builder);
	}
	
	@Test
	public void testIndexedPrimitivesWithSetterAccess() throws Exception {
		ResultSetMapperBuilder<DbPrimitiveObjectWithSetter> builder = new ResultSetMapperBuilderImpl<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class);
		testIndexedPrimitives(builder);
	}
	
	@Test
	public void testNamedBoxedPrimitivesWithNullValues() throws Exception {
		ResultSetMapperBuilder<DbBoxedPrimitveObject> builder = new ResultSetMapperBuilderImpl<DbBoxedPrimitveObject>(DbBoxedPrimitveObject.class);
		testNamedPrimitivesWithNull(builder);
	}
	
	@Test
	public void testIndexedPrimitivesWithNullValues() throws Exception {
		ResultSetMapperBuilder<DbBoxedPrimitveObject> builder = new ResultSetMapperBuilderImpl<DbBoxedPrimitveObject>(DbBoxedPrimitveObject.class);
		testIndexedPrimitivesWithNull(builder);
	}
	
	@Test
	public void testIndexedPrimitivesWithSetterAccessNoAsm() throws Exception {
		ResultSetMapperBuilder<DbPrimitiveObjectWithSetter> builder = new ResultSetMapperBuilderImpl<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class, new SetterFactory(null));
		testIndexedPrimitives(builder);
	}
	
	@Test
	public void testIndexedBoxedPrimitives() throws Exception {
		ResultSetMapperBuilder<DbBoxedPrimitveObject> builder = new ResultSetMapperBuilderImpl<DbBoxedPrimitveObject>(DbBoxedPrimitveObject.class);
		testIndexedPrimitives(builder);
	}
	
	private <T extends PrimitiveObject> void testIndexedPrimitives(ResultSetMapperBuilder<T> builder)
			throws SQLException, Exception {
		addIndexedColumn(builder);		
		testPrimitives(builder.mapper());
	}
	private void testIndexedPrimitivesWithNull(ResultSetMapperBuilder<DbBoxedPrimitveObject> builder)
			throws SQLException, Exception {
		addIndexedColumn(builder);		
		testPrimitivesWithNullValues(builder.mapper());
	}
	private <T extends PrimitiveObject> void addIndexedColumn(
			ResultSetMapperBuilder<T> builder) {
		builder
			.addMapping("pBoolean", 1)
			.addMapping("pByte", 2)
			.addMapping("pCharacter", 3)
			.addMapping("pShort", 4)
			.addMapping("pInt", 5)
			.addMapping("pLong", 6)
			.addMapping("pFloat", 7)
			.addMapping("pDouble", 8);
	}
	private <T extends PrimitiveObject> void testNamedPrimitives(ResultSetMapperBuilder<T> builder)
			throws SQLException, Exception {
		addNamedColumns(builder);		
		testPrimitives(builder.mapper());
	}
	
	private void testNamedPrimitivesWithNull(ResultSetMapperBuilder<DbBoxedPrimitveObject> builder)
			throws SQLException, Exception {
		addNamedColumns(builder);		
		testPrimitivesWithNullValues(builder.mapper());
	}


	private <T extends PrimitiveObject> void addNamedColumns(
			ResultSetMapperBuilder<T> builder) {
		builder.addNamedColumn("p_boolean");
		builder.addNamedColumn("p_byte");
		builder.addNamedColumn("p_character");
		builder.addNamedColumn("p_short");
		builder.addNamedColumn("p_int");
		builder.addNamedColumn("p_long");
		builder.addNamedColumn("p_float");
		builder.addNamedColumn("p_double");
	}

	public <T extends PrimitiveObject> void testPrimitives(Mapper<ResultSet, T> mapper) throws SQLException, Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getBoolean("p_boolean")).thenReturn(true);
		when(rs.getByte("p_byte")).thenReturn((byte)0xa3);
		when(rs.getInt("p_character")).thenReturn(0xa4);
		when(rs.getShort("p_short")).thenReturn((short)0xa5);
		when(rs.getInt("p_int")).thenReturn(0xa6);
		when(rs.getLong("p_long")).thenReturn(0xffa4l);
		when(rs.getFloat("p_float")).thenReturn(3.14f);
		when(rs.getDouble("p_double")).thenReturn(3.14159);
		
		when(rs.getBoolean(1)).thenReturn(true);
		when(rs.getByte(2)).thenReturn((byte)0xa3);
		when(rs.getInt(3)).thenReturn(0xa4);
		when(rs.getShort(4)).thenReturn((short)0xa5);
		when(rs.getInt(5)).thenReturn(0xa6);
		when(rs.getLong(6)).thenReturn(0xffa4l);
		when(rs.getFloat(7)).thenReturn(3.14f);
		when(rs.getDouble(8)).thenReturn(3.14159);
		
		T object = mapper.map(rs);
		
		assertEquals(true,  object.ispBoolean());
		assertEquals((byte)0xa3, object.getpByte());
		assertEquals((char)0xa4, object.getpCharacter());
		assertEquals((short)0xa5, object.getpShort());
		assertEquals((int)0xa6, object.getpInt());
		assertEquals((long)0xffa4l, object.getpLong());
		assertEquals((float)3.14f, object.getpFloat(), 0);
		assertEquals((double)3.14159, object.getpDouble(), 0);
	}
	
	public  void testPrimitivesWithNullValues(
			Mapper<ResultSet, DbBoxedPrimitveObject> mapper) throws SQLException, Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.wasNull()).thenReturn(true);
		
		when(rs.getBoolean("p_boolean")).thenReturn(false);
		when(rs.getByte("p_byte")).thenReturn((byte)0);
		when(rs.getInt("p_character")).thenReturn(0);
		when(rs.getShort("p_short")).thenReturn((short)0);
		when(rs.getInt("p_int")).thenReturn(0);
		when(rs.getLong("p_long")).thenReturn(0l);
		when(rs.getFloat("p_float")).thenReturn(0f);
		when(rs.getDouble("p_double")).thenReturn(0.0);
		
		when(rs.getBoolean(1)).thenReturn(true);
		when(rs.getByte(2)).thenReturn((byte)0);
		when(rs.getInt(3)).thenReturn(0);
		when(rs.getShort(4)).thenReturn((short)0);
		when(rs.getInt(5)).thenReturn(0);
		when(rs.getLong(6)).thenReturn(0l);
		when(rs.getFloat(7)).thenReturn(0f);
		when(rs.getDouble(8)).thenReturn(0.0);
		
		DbBoxedPrimitveObject object = mapper.map(rs);
		
		assertNull(object.getoBoolean());
		assertNull(object.getoByte());
		assertNull(object.getoCharacter());
		assertNull(object.getoShort());
		assertNull(object.getoInt());
		assertNull(object.getoLong());
		assertNull(object.getoFloat());
		assertNull(object.getoDouble());
	}
	
	private void testDbObjectMappingFromDb(final JdbcMapper<DbObject> mapper)
			throws SQLException, Exception, ParseException {
		
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				rs.next();
				DbHelper.assertDbObjectMapping(mapper.map(rs));
			}
		});
		
	}

	@Test
	public void testHandleMapperErrorSetterNotFound() throws NoSuchMethodException, SecurityException {
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
	public void testHandleMapperErrorgGetterNotFound() throws NoSuchMethodException, SecurityException {
		ResultSetMapperBuilder<MyClass> builder = new ResultSetMapperBuilderImpl<MyClass>(MyClass.class);
		MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);
		
		builder.mapperBuilderErrorHandler(errorHandler);
		
		builder.addMapping("prop", 1);
		
		verify(errorHandler).getterNotFound("No getter for column 1 type class org.sfm.beans.Foo");
		
		builder.addMapping("prop", "col");
		
		verify(errorHandler).getterNotFound("No getter for column 1 type class org.sfm.beans.Foo");
		
		builder.addIndexedColumn("prop");
		
		verify(errorHandler).getterNotFound("No getter for column 1 type class org.sfm.beans.Foo");
		
		builder.addNamedColumn("prop");
		
		verify(errorHandler).getterNotFound("No getter for column 1 type class org.sfm.beans.Foo");
	}
	
	@Test
	public void setChangeFieldMapperErrorHandler() throws NoSuchMethodException, SecurityException {
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
