package org.sfm.jdbc;

import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JdbcMapperOneConstructorTypeTest {

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
		JdbcMapperBuilder<MyObject> builder = JdbcMapperFactoryHelper.disableAsm().newBuilder(MyObject.class);
		testMatchConstructor(builder);
	}


	@Test
	public void testCanCreateTypeFromUnambiguousConstructorAsm() throws Exception {
		JdbcMapperBuilder<MyObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(MyObject.class);
		testMatchConstructor(builder);
	}
	
	@Test
	public void testCanCreateTypeFromAmbiguousWithType() throws Exception {
		JdbcMapperBuilder<MyObjectAmbiguity> builder = JdbcMapperFactoryHelper.asm().newBuilder(MyObjectAmbiguity.class);
		builder.addMapping("prop", 1, Types.VARCHAR);
		JdbcMapper<MyObjectAmbiguity> mapper = builder.mapper();
		ResultSet rs = mock(ResultSet.class);
		when(rs.getString(1)).thenReturn("val");
		assertEquals("val", mapper.map(rs).prop.value);
	}	
	private void testMatchConstructor(JdbcMapperBuilder<MyObject> builder)
			throws SQLException {
		builder.addMapping("prop");
		JdbcMapper<MyObject> mapper = builder.mapper();
		ResultSet rs = mock(ResultSet.class);
		when(rs.getString(1)).thenReturn("val");
		assertEquals("val", mapper.map(rs).prop.value);
	}
}
