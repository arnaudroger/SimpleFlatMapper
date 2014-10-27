package org.sfm.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.junit.Test;
import org.sfm.reflect.ReflectionService;

public class JdbcMapperOneConstructorType {

	public static class MyObject {
		SubObject prop;
	}
	public static class SubObject {
		private final String value;

		public SubObject(String value) {
			this.value = value;
		}
	}
	
	public static class MyObjectAmbiguity {
		SubObjectAmbiguity prop;
	}
	public static class SubObjectAmbiguity {
		@SuppressWarnings("unused")
		private final String value;

		public SubObjectAmbiguity(String value) {
			this.value = value;
		}
		
		public SubObjectAmbiguity(String value, int ivalue) {
			this.value = value;
		}
	}
	
	@Test
	public void testCanCreateTypeFromUnambiguousConstructorNoAsm() throws Exception {
		ResultSetMapperBuilder<MyObject> builder = new ResultSetMapperBuilderImpl<MyObject>(MyObject.class, new ReflectionService(false, false));
		testMatchConstructor(builder);
	}

	@Test
	public void testCantCreateTypeFromAmbiguousConstructor() throws Exception {

		ResultSetMapperBuilder<MyObjectAmbiguity> builder = new ResultSetMapperBuilderImpl<MyObjectAmbiguity>(MyObjectAmbiguity.class, new ReflectionService(false, false));
		
		try {
			builder.addIndexedColumn("prop").mapper();
			fail("Cannot map with ambiguous constructor");
		} catch(Exception e) {
			// expected
		}
	}

	
	@Test
	public void testCanCreateTypeFromUnambiguousConstructorAsm() throws Exception {
		ResultSetMapperBuilder<MyObject> builder = new ResultSetMapperBuilderImpl<MyObject>(MyObject.class, new ReflectionService(true, true));
		testMatchConstructor(builder);
	}
	
	@Test
	public void testCanCreateTypeFromAmbiguousWithType() throws Exception {
		ResultSetMapperBuilder<MyObject> builder = new ResultSetMapperBuilderImpl<MyObject>(MyObject.class, new ReflectionService(true, true));
		builder.addIndexedColumn("prop", 1, Types.VARCHAR);
		JdbcMapper<MyObject> mapper = builder.mapper();
		ResultSet rs = mock(ResultSet.class);
		when(rs.getString(1)).thenReturn("val");
		assertEquals("val", mapper.map(rs).prop.value);
	}	
	private void testMatchConstructor(ResultSetMapperBuilder<MyObject> builder)
			throws SQLException {
		builder.addIndexedColumn("prop");
		JdbcMapper<MyObject> mapper = builder.mapper();
		ResultSet rs = mock(ResultSet.class);
		when(rs.getString(1)).thenReturn("val");
		assertEquals("val", mapper.map(rs).prop.value);
	}
}
