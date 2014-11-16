package org.sfm.jdbc.impl.getter;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.Types;

import org.junit.Before;
import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.JdbcColumnKey;

public class ResultSetGetterFactoryTest {

	ResultSetGetterFactory factory;
	ResultSet resultSet;
	
	@Before
	public void setUp() {
		factory = new ResultSetGetterFactory();
		resultSet = mock(ResultSet.class);
	}
	
	@Test
	public void testNString() throws Exception {
		when(resultSet.getNString(1)).thenReturn("value");
		assertEquals("value", factory.newGetter(String.class, key(Types.NCHAR)).get(resultSet));
		assertEquals("value", factory.newGetter(String.class, key(Types.NCLOB)).get(resultSet));
		assertEquals("value", factory.newGetter(String.class, key(Types.NVARCHAR)).get(resultSet));
	}
	
	@Test
	public void testEnumNString() throws Exception {
		when(resultSet.getNString(1)).thenReturn("type4");
		assertEquals(DbObject.Type.type4, factory.newGetter(DbObject.Type.class, key(Types.NCHAR)).get(resultSet));
		assertEquals(DbObject.Type.type4, factory.newGetter(DbObject.Type.class, key(Types.NCLOB)).get(resultSet));
		assertEquals(DbObject.Type.type4, factory.newGetter(DbObject.Type.class, key(Types.NVARCHAR)).get(resultSet));

		when(resultSet.getString(1)).thenReturn("type3");
		assertEquals(DbObject.Type.type3, factory.newGetter(DbObject.Type.class, key(Types.CHAR)).get(resultSet));
		assertEquals(DbObject.Type.type3, factory.newGetter(DbObject.Type.class, key(Types.CLOB)).get(resultSet));
		assertEquals(DbObject.Type.type3, factory.newGetter(DbObject.Type.class, key(Types.VARCHAR)).get(resultSet));
	}
	
	private JdbcColumnKey key(int type) {
		return new JdbcColumnKey("NA", 1, type);
	}

}
