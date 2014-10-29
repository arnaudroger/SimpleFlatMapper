package org.sfm.jdbc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.Timestamp;

import org.junit.Test;
import org.sfm.beans.DbConstructorObject;
import org.sfm.reflect.ReflectionService;

public class JdbcMapperConstructorInjectionTest {
	
	private ReflectionService nonAsmReflectionService = new ReflectionService(false, false);
	private ReflectionService asmDeactivatedReflectionService = new ReflectionService(true, false);
	private ReflectionService asmReflectionService = new ReflectionService(true, true);

	@Test
	public void testChooseSmallestMatchingConstructor() throws Exception {
		JdbcMapperBuilder<DbConstructorObject> builder = new JdbcMapperBuilder<DbConstructorObject>(DbConstructorObject.class, asmReflectionService);
		
		builder.addMapping("prop1");
		
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
		JdbcMapperBuilder<DbConstructorObject> builder = new JdbcMapperBuilder<DbConstructorObject>(DbConstructorObject.class, asmReflectionService);
		
		builder.addMapping("prop1");
		builder.addMapping("prop2");
		
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
		JdbcMapperBuilder<DbConstructorObject> builder = new JdbcMapperBuilder<DbConstructorObject>(DbConstructorObject.class, asmReflectionService);
		
		builder.addMapping("prop1");
		builder.addMapping("prop3");
		
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
		JdbcMapperBuilder<DbConstructorObject> builder = new JdbcMapperBuilder<DbConstructorObject>(DbConstructorObject.class, asmReflectionService);
		
		builder.addMapping("prop3");
		
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
		JdbcMapperBuilder<DbConstructorObject> builder = new JdbcMapperBuilder<DbConstructorObject>(DbConstructorObject.class, asmReflectionService);
		
		builder.addMapping("prop1");
		builder.addMapping("prop2");
		try {
			builder.addMapping("prop3");
			fail("Expect exception");
		} catch(Exception e) {
			// expected
		}
	}
	
	
	
	@Test
	public void testChooseSmallestMatchingConstructoNoAsm() throws Exception {
		JdbcMapperBuilder<DbConstructorObject> builder = new JdbcMapperBuilder<DbConstructorObject>(DbConstructorObject.class, asmDeactivatedReflectionService );
		
		builder.addMapping("prop1");
		
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
		JdbcMapperBuilder<DbConstructorObject> builder = new JdbcMapperBuilder<DbConstructorObject>(DbConstructorObject.class, asmDeactivatedReflectionService);
		
		builder.addMapping("prop1");
		builder.addMapping("prop2");
		
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
		JdbcMapperBuilder<DbConstructorObject> builder = new JdbcMapperBuilder<DbConstructorObject>(DbConstructorObject.class, asmDeactivatedReflectionService);
		
		builder.addMapping("prop1");
		builder.addMapping("prop3");
		
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
		JdbcMapperBuilder<DbConstructorObject> builder = new JdbcMapperBuilder<DbConstructorObject>(DbConstructorObject.class, asmDeactivatedReflectionService);
		
		builder.addMapping("prop3");
		
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
		JdbcMapperBuilder<DbConstructorObject> builder = new JdbcMapperBuilder<DbConstructorObject>(DbConstructorObject.class, nonAsmReflectionService);
		
		try {
			builder.addMapping("prop1");
			fail("Expect exception");
		} catch(Exception e) {
			// expected
		}
	}
	
	
}
