package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.test.beans.DbBoxedPrimitiveObject;
import org.simpleflatmapper.test.beans.DbFinalPrimitiveObject;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.test.beans.PrimitiveObject;
import org.simpleflatmapper.map.SourceMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JdbcMapperPrimitiveTest {
	

	@Test
	public void testIndexedPrimitivesWithSetterAccess() throws Exception {
		JdbcMapperBuilder<DbPrimitiveObjectWithSetter> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbPrimitiveObjectWithSetter.class);
		testIndexedPrimitives(builder);
	}
	
	@Test
	public void testIndexedPrimitivesWithConstructorAccess() throws Exception {
		JdbcMapperBuilder<DbFinalPrimitiveObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbFinalPrimitiveObject.class);
		testIndexedPrimitives(builder);
	}
	
	
	@Test
	public void testIndexedPrimitivesWithSetterAccessNoAsm() throws Exception {
		JdbcMapperBuilder<DbPrimitiveObjectWithSetter> builder = JdbcMapperFactoryHelper.noAsm().newBuilder(DbPrimitiveObjectWithSetter.class);
		testIndexedPrimitives(builder);
	}
	
	@Test
	public void testIndexedBoxedPrimitivesWithFieldAccess() throws Exception {
		JdbcMapperBuilder<DbBoxedPrimitiveObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbBoxedPrimitiveObject.class);
		testIndexedPrimitives(builder);
	}
	
	@Test
	public void testIndexedPrimitivesWithFieldAccessNullValues() throws Exception {
		JdbcMapperBuilder<DbBoxedPrimitiveObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbBoxedPrimitiveObject.class);
		testIndexedPrimitivesWithNull(builder);
	}
	
	

	private <T extends PrimitiveObject> void testIndexedPrimitives(JdbcMapperBuilder<T> builder)
			throws SQLException, Exception {
		addIndexedColumn(builder);		
		testPrimitives(builder.mapper());
	}
	private void testIndexedPrimitivesWithNull(JdbcMapperBuilder<DbBoxedPrimitiveObject> builder)
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
	

	public <T extends PrimitiveObject> void testPrimitives(SourceMapper<ResultSet, T> mapper) throws SQLException, Exception {
		ResultSet rs = mock(ResultSet.class);

		when(rs.getBoolean(1)).thenReturn(true);
		when(rs.getByte(2)).thenReturn((byte)0xa3);
		when(rs.getInt(3)).thenReturn(0xa4);
		when(rs.getShort(4)).thenReturn((short)0xa5);
		when(rs.getInt(5)).thenReturn(0xa6);
		when(rs.getLong(6)).thenReturn(0xffa4l);
		when(rs.getFloat(7)).thenReturn(3.14f);
		when(rs.getDouble(8)).thenReturn(3.14159);
		
		T object = mapper.map(rs, null);
		
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
			SourceMapper<ResultSet, DbBoxedPrimitiveObject> mapper) throws SQLException, Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.wasNull()).thenReturn(true);
		
		when(rs.getBoolean(1)).thenReturn(false);
		when(rs.getByte(2)).thenReturn((byte)0);
		when(rs.getInt(3)).thenReturn(0);
		when(rs.getShort(4)).thenReturn((short)0);
		when(rs.getInt(5)).thenReturn(0);
		when(rs.getLong(6)).thenReturn(0l);
		when(rs.getFloat(7)).thenReturn(0f);
		when(rs.getDouble(8)).thenReturn(0d);
		
		DbBoxedPrimitiveObject object = mapper.map(rs, null);
		
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
