package org.sfm.jdbc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.sfm.beans.DbBoxedPrimitveObject;
import org.sfm.beans.DbFinalPrimitiveObject;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.beans.PrimitiveObject;
import org.sfm.map.Mapper;
import org.sfm.reflect.ReflectionService;

public class JdbcMapperPrimitiveTest {
	
	private ReflectionService nonAsmReflectionService = ReflectionService.newInstance(true, false);
	private ReflectionService asmReflectionService = ReflectionService.newInstance(false, true);

	@Test
	public void testIndexedPrimitivesWithSetterAccess() throws Exception {
		JdbcMapperBuilder<DbPrimitiveObjectWithSetter> builder = new JdbcMapperBuilder<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class, asmReflectionService);
		testIndexedPrimitives(builder);
	}
	
	@Test
	public void testIndexedPrimitivesWithConstructorAccess() throws Exception {
		JdbcMapperBuilder<DbFinalPrimitiveObject> builder = new JdbcMapperBuilder<DbFinalPrimitiveObject>(DbFinalPrimitiveObject.class, asmReflectionService);
		testIndexedPrimitives(builder);
	}
	
	
	@Test
	public void testIndexedPrimitivesWithSetterAccessNoAsm() throws Exception {
		JdbcMapperBuilder<DbPrimitiveObjectWithSetter> builder = new JdbcMapperBuilder<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class, nonAsmReflectionService);
		testIndexedPrimitives(builder);
	}
	
	@Test
	public void testIndexedBoxedPrimitivesWithFieldAccess() throws Exception {
		JdbcMapperBuilder<DbBoxedPrimitveObject> builder = new JdbcMapperBuilder<DbBoxedPrimitveObject>(DbBoxedPrimitveObject.class, asmReflectionService);
		testIndexedPrimitives(builder);
	}
	
	@Test
	public void testIndexedPrimitivesWithFieldAccessNullValues() throws Exception {
		JdbcMapperBuilder<DbBoxedPrimitveObject> builder = new JdbcMapperBuilder<DbBoxedPrimitveObject>(DbBoxedPrimitveObject.class, asmReflectionService);
		testIndexedPrimitivesWithNull(builder);
	}
	
	

	private <T extends PrimitiveObject> void testIndexedPrimitives(JdbcMapperBuilder<T> builder)
			throws SQLException, Exception {
		addIndexedColumn(builder);		
		testPrimitives(builder.mapper());
	}
	private void testIndexedPrimitivesWithNull(JdbcMapperBuilder<DbBoxedPrimitveObject> builder)
			throws SQLException, Exception {
		addIndexedColumn(builder);		
		testPrimitivesWithNullValues(builder.mapper());
	}
	private <T extends PrimitiveObject> void addIndexedColumn(
			JdbcMapperBuilder<T> builder) {
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
	
	public void testPrimitivesWithNullValues(
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
}
