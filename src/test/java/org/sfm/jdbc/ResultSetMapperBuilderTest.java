package org.sfm.jdbc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import org.junit.Test;
import org.sfm.beans.DbBoxedPrimitveObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.beans.PrimitiveObject;
import org.sfm.map.Mapper;
import org.sfm.utils.Handler;

public class ResultSetMapperBuilderTest {

	@Test
	public void testSelectWithManualDefinition() throws Exception {
		Mapper<ResultSet, DbObject> mapper = newManualMapper();
		testDbObjectMappingFromDb(mapper);
	}

	public static  ResultSetMapper<DbObject> newManualMapper() throws NoSuchMethodException, SecurityException {
		ResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilder<DbObject>(DbObject.class);
		
		builder.addMapping("id", "id");
		builder.addMapping("name", "name");
		builder.addMapping("email", "email");
		builder.addMapping("creationTime", "creation_time");
		
		return builder.mapper();
	}
	
	@Test
	public void testSelectWithManualColumnDefinition() throws Exception {
		ResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilder<DbObject>(DbObject.class);
		
		builder.addColumn("id");
		builder.addColumn("name");
		builder.addColumn("email");
		builder.addColumn("creation_time");
		
		Mapper<ResultSet, DbObject> mapper = builder.mapper();
		
		testDbObjectMappingFromDb(mapper);
	}
	
	@Test
	public void testSelectAdaptive() throws Exception {
		ResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilder<DbObject>(DbObject.class);
		
		Mapper<ResultSet, DbObject> mapper = builder.mapper();
		
		testDbObjectMappingFromDb(mapper);
	}
	
	@Test
	public void testNamedPrimitivesWithSetterAccess() throws Exception {
		ResultSetMapperBuilder<DbPrimitiveObjectWithSetter> builder = new ResultSetMapperBuilder<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class);
		testNamedPrimitives(builder, new DbPrimitiveObjectWithSetter());
	}
	
	@Test
	public void testNamedBoxedPrimitives() throws Exception {
		ResultSetMapperBuilder<DbBoxedPrimitveObject> builder = new ResultSetMapperBuilder<DbBoxedPrimitveObject>(DbBoxedPrimitveObject.class);
		testNamedPrimitives(builder, new DbBoxedPrimitveObject());
	}
	
	@Test
	public void testIndexedPrimitivesWithSetterAccess() throws Exception {
		ResultSetMapperBuilder<DbPrimitiveObjectWithSetter> builder = new ResultSetMapperBuilder<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class);
		testIndexedPrimitives(builder, new DbPrimitiveObjectWithSetter());
	}
	
	@Test
	public void testIndexedBoxedPrimitives() throws Exception {
		ResultSetMapperBuilder<DbBoxedPrimitveObject> builder = new ResultSetMapperBuilder<DbBoxedPrimitveObject>(DbBoxedPrimitveObject.class);
		testIndexedPrimitives(builder, new DbBoxedPrimitveObject());
	}
	
	private <T extends PrimitiveObject> void testIndexedPrimitives(ResultSetMapperBuilder<T> builder, T object)
			throws SQLException, Exception {
		Mapper<ResultSet, T> mapper  = builder
			.addMapping("pBoolean", 1)
			.addMapping("pByte", 2)
			.addMapping("pCharacter", 3)
			.addMapping("pShort", 4)
			.addMapping("pInt", 5)
			.addMapping("pLong", 6)
			.addMapping("pFloat", 7)
			.addMapping("pDouble", 8).mapper();		
		testPrimitives(object, mapper);
	}
	private <T extends PrimitiveObject> void testNamedPrimitives(ResultSetMapperBuilder<T> builder, T object)
			throws SQLException, Exception {
		builder.addColumn("p_boolean");
		builder.addColumn("p_byte");
		builder.addColumn("p_character");
		builder.addColumn("p_short");
		builder.addColumn("p_int");
		builder.addColumn("p_long");
		builder.addColumn("p_float");
		builder.addColumn("p_double");		
		
		Mapper<ResultSet, T> mapper = builder.mapper();
		
		testPrimitives(object, mapper);
	}

	public <T extends PrimitiveObject> void testPrimitives(T object,
			Mapper<ResultSet, T> mapper) throws SQLException, Exception {
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
		
		mapper.map(rs, object);
		
		assertEquals(true,  object.ispBoolean());
		assertEquals((byte)0xa3, object.getpByte());
		assertEquals((char)0xa4, object.getpCharacter());
		assertEquals((short)0xa5, object.getpShort());
		assertEquals((int)0xa6, object.getpInt());
		assertEquals((long)0xffa4l, object.getpLong());
		assertEquals((float)3.14f, object.getpFloat(), 0);
		assertEquals((double)3.14159, object.getpDouble(), 0);
	}
	
	private void testDbObjectMappingFromDb(final Mapper<ResultSet, DbObject> mapper)
			throws SQLException, Exception, ParseException {
		
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				DbObject dbObject = new DbObject();
				rs.next();
				mapper.map(rs, dbObject);
				DbHelper.assertDbObjectMapping(dbObject);
			}
		});
		
	}

	
}
