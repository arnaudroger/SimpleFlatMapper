package org.sfm.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.Timestamp;

import org.junit.Test;
import org.sfm.beans.DbConstructorObject;
import org.sfm.reflect.SetterFactory;

public class JdbcMapperConstructorInjectionTest {
	
	@Test
	public void testChooseSmallestMatchingConstructor() throws Exception {
		ResultSetMapperBuilder<DbConstructorObject> builder = new ResultSetMapperBuilderImpl<>(DbConstructorObject.class);
		
		builder.addIndexedColumn("prop1");
		
		JdbcMapper<DbConstructorObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.getString(1)).thenReturn("propValue");
		
		DbConstructorObject  o = mapper.map(rs);
		
		assertEquals("propValue", o.getProp1());
		assertNull(o.getProp2());
		assertNull(o.getProp3());
		assertEquals(0, o.getC());
	}
	
	@Test
	public void testConstructorProp1Prop2() throws Exception {
		ResultSetMapperBuilder<DbConstructorObject> builder = new ResultSetMapperBuilderImpl<>(DbConstructorObject.class);
		
		builder.addIndexedColumn("prop1");
		builder.addIndexedColumn("prop2");
		
		JdbcMapper<DbConstructorObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.getString(1)).thenReturn("propValue1");
		when(rs.getString(2)).thenReturn("propValue2");
		
		DbConstructorObject  o = mapper.map(rs);
		
		assertEquals("propValue1", o.getProp1());
		assertEquals("propValue2", o.getProp2());
		assertNull(o.getProp3());
		assertEquals(1, o.getC());
	}
	
	@Test
	public void testConstructorProp1Prop3() throws Exception {
		ResultSetMapperBuilder<DbConstructorObject> builder = new ResultSetMapperBuilderImpl<>(DbConstructorObject.class);
		
		builder.addIndexedColumn("prop1");
		builder.addIndexedColumn("prop3");
		
		JdbcMapper<DbConstructorObject> mapper = builder.mapper();
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.getString(1)).thenReturn("propValue1");
		when(rs.getTimestamp(2)).thenReturn(ts);
		
		DbConstructorObject  o = mapper.map(rs);
		
		assertEquals("propValue1", o.getProp1());
		assertNull(o.getProp2());
		assertEquals(ts, o.getProp3());
		assertEquals(2, o.getC());
	}
	
	@Test
	public void testConstructorProp3() throws Exception {
		ResultSetMapperBuilder<DbConstructorObject> builder = new ResultSetMapperBuilderImpl<>(DbConstructorObject.class);
		
		builder.addIndexedColumn("prop3");
		
		JdbcMapper<DbConstructorObject> mapper = builder.mapper();
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.getTimestamp(1)).thenReturn(ts);
		
		DbConstructorObject  o = mapper.map(rs);
		
		assertNull(o.getProp1());
		assertNull(o.getProp2());
		assertEquals(ts, o.getProp3());
		assertEquals(2, o.getC());
	}	
	@Test
	public void testConstructorProp1Prop2Prop3() throws Exception {
		ResultSetMapperBuilder<DbConstructorObject> builder = new ResultSetMapperBuilderImpl<>(DbConstructorObject.class);
		
		builder.addIndexedColumn("prop1");
		builder.addIndexedColumn("prop2");
		try {
			builder.addIndexedColumn("prop3");
			fail("Expect exception");
		} catch(Exception e) {
			// expected
		}
	}
	
	
	
	@Test
	public void testChooseSmallestMatchingConstructoNoAsmr() throws Exception {
		ResultSetMapperBuilder<DbConstructorObject> builder = new ResultSetMapperBuilderImpl<>(DbConstructorObject.class, new SetterFactory(null), true);
		
		builder.addIndexedColumn("prop1");
		
		JdbcMapper<DbConstructorObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.getString(1)).thenReturn("propValue");
		
		DbConstructorObject  o = mapper.map(rs);
		
		assertEquals("propValue", o.getProp1());
		assertNull(o.getProp2());
		assertNull(o.getProp3());
		assertEquals(0, o.getC());
	}
	
	@Test
	public void testConstructorProp1Prop2NoAsm() throws Exception {
		ResultSetMapperBuilder<DbConstructorObject> builder = new ResultSetMapperBuilderImpl<>(DbConstructorObject.class, new SetterFactory(null), true);
		
		builder.addIndexedColumn("prop1");
		builder.addIndexedColumn("prop2");
		
		JdbcMapper<DbConstructorObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.getString(1)).thenReturn("propValue1");
		when(rs.getString(2)).thenReturn("propValue2");
		
		DbConstructorObject  o = mapper.map(rs);
		
		assertEquals("propValue1", o.getProp1());
		assertEquals("propValue2", o.getProp2());
		assertNull(o.getProp3());
		assertEquals(1, o.getC());
	}
	
	@Test
	public void testConstructorProp1Prop3NoAsm() throws Exception {
		ResultSetMapperBuilder<DbConstructorObject> builder = new ResultSetMapperBuilderImpl<>(DbConstructorObject.class, new SetterFactory(null), true);
		
		builder.addIndexedColumn("prop1");
		builder.addIndexedColumn("prop3");
		
		JdbcMapper<DbConstructorObject> mapper = builder.mapper();
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.getString(1)).thenReturn("propValue1");
		when(rs.getTimestamp(2)).thenReturn(ts);
		
		DbConstructorObject  o = mapper.map(rs);
		
		assertEquals("propValue1", o.getProp1());
		assertNull(o.getProp2());
		assertEquals(ts, o.getProp3());
		assertEquals(2, o.getC());
	}
	
	@Test
	public void testConstructorProp3NoAsm() throws Exception {
		ResultSetMapperBuilder<DbConstructorObject> builder = new ResultSetMapperBuilderImpl<>(DbConstructorObject.class, new SetterFactory(null), true);
		
		builder.addIndexedColumn("prop3");
		
		JdbcMapper<DbConstructorObject> mapper = builder.mapper();
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.getTimestamp(1)).thenReturn(ts);
		
		DbConstructorObject  o = mapper.map(rs);
		
		assertNull(o.getProp1());
		assertNull(o.getProp2());
		assertEquals(ts, o.getProp3());
		assertEquals(2, o.getC());
	}	
	
	@Test
	public void testConstructorNoAsmAndNoAsmLinb() throws Exception {
		ResultSetMapperBuilder<DbConstructorObject> builder = new ResultSetMapperBuilderImpl<>(DbConstructorObject.class, new SetterFactory(null), false);
		
		try {
			builder.addIndexedColumn("prop1");
			fail("Expect exception");
		} catch(Exception e) {
			// expected
		}
	}
	
	
}
