package org.sfm.jdbc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.Types;

import org.junit.Test;
import org.sfm.beans.DbEnumObject;
import org.sfm.beans.DbObject.Type;

public class JdbcMapperEnumTest {

	@Test
	public void testNamedEnumUndefined() throws Exception {
		ResultSetMapperBuilder<DbEnumObject> builder = new ResultSetMapperBuilderImpl<DbEnumObject>(DbEnumObject.class);
		builder.addNamedColumn("val");
		
		JdbcMapper<DbEnumObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		
		when(rs.getObject("val")).thenReturn(Integer.valueOf(2));
		
		assertEquals(Type.type3, mapper.map(rs).getVal());

		when(rs.getObject("val")).thenReturn("type2");
		
		assertEquals(Type.type2, mapper.map(rs).getVal());
		
	}

	@Test
	public void testIndexedEnumUndefined() throws Exception {
		ResultSetMapperBuilder<DbEnumObject> builder = new ResultSetMapperBuilderImpl<DbEnumObject>(DbEnumObject.class);
		builder.addIndexedColumn("val", 1);
		
		JdbcMapper<DbEnumObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		
		when(rs.getObject(1)).thenReturn(Integer.valueOf(2));
		
		assertEquals(Type.type3, mapper.map(rs).getVal());

		when(rs.getObject(1)).thenReturn("type2");
		
		assertEquals(Type.type2, mapper.map(rs).getVal());
	}
	
	
	@Test
	public void testNamedEnumString() throws Exception {
		ResultSetMapperBuilder<DbEnumObject> builder = new ResultSetMapperBuilderImpl<DbEnumObject>(DbEnumObject.class);
		builder.addNamedColumn("val", Types.VARCHAR);
		
		JdbcMapper<DbEnumObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		
		when(rs.getString("val")).thenReturn("type2");
		
		assertEquals(Type.type2, mapper.map(rs).getVal());
		
	}
	
	@Test
	public void testIndexedEnumString() throws Exception {
		ResultSetMapperBuilder<DbEnumObject> builder = new ResultSetMapperBuilderImpl<DbEnumObject>(DbEnumObject.class);
		builder.addIndexedColumn("val",1, Types.VARCHAR);
		
		JdbcMapper<DbEnumObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		
		when(rs.getString(1)).thenReturn("type2");
		
		assertEquals(Type.type2, mapper.map(rs).getVal());
		
	}
	
	@Test
	public void testNamedEnumOrdinal() throws Exception {
		ResultSetMapperBuilder<DbEnumObject> builder = new ResultSetMapperBuilderImpl<DbEnumObject>(DbEnumObject.class);
		builder.addNamedColumn("val", Types.INTEGER);
		
		JdbcMapper<DbEnumObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		
		when(rs.getInt("val")).thenReturn(2);
		
		assertEquals(Type.type3, mapper.map(rs).getVal());
		
	}
	
	@Test
	public void testIndexedEnumOrdinal() throws Exception {
		ResultSetMapperBuilder<DbEnumObject> builder = new ResultSetMapperBuilderImpl<DbEnumObject>(DbEnumObject.class);
		builder.addIndexedColumn("val",1, Types.INTEGER);
		
		JdbcMapper<DbEnumObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		
		when(rs.getInt(1)).thenReturn(2);
		
		assertEquals(Type.type3, mapper.map(rs).getVal());
		
	}

}
