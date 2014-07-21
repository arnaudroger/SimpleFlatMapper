package org.sfm.jdbc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import org.junit.Test;
import org.sfm.beans.DbBoxedPrimitveObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPrimitiveObject;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.beans.PrimitiveObject;
import org.sfm.jdbc.ResultSetMapperBuilder;
import org.sfm.map.Mapper;
import org.sfm.utils.DateHelper;

public class ResultSetMapperBuilderTest {

	@Test
	public void testSelectWithManualDefinition() throws Exception {
		
		ResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilder<DbObject>(DbObject.class);
		
		builder.addMapping("id", "id");
		builder.addMapping("name", "name");
		builder.addMapping("email", "email");
		builder.addMapping("creationTime", "creation_time");
		
		Mapper<ResultSet, DbObject> mapper = builder.mapper();
		
		testDbObjectMappingFromDb(mapper);
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
	public void testPrimitivesWithFieldAccess() throws Exception {
		ResultSetMapperBuilder<DbPrimitiveObject> builder = new ResultSetMapperBuilder<DbPrimitiveObject>(DbPrimitiveObject.class);
		testPrimitives(builder, new DbPrimitiveObject());
	}
	@Test
	public void testPrimitivesWithSetterAccess() throws Exception {
		ResultSetMapperBuilder<DbPrimitiveObjectWithSetter> builder = new ResultSetMapperBuilder<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class);
		testPrimitives(builder, new DbPrimitiveObjectWithSetter());
	}
	@Test
	public void testBoxedPrimitives() throws Exception {
		ResultSetMapperBuilder<DbBoxedPrimitveObject> builder = new ResultSetMapperBuilder<DbBoxedPrimitveObject>(DbBoxedPrimitveObject.class);
		testPrimitives(builder, new DbBoxedPrimitveObject());
	}
	private <T extends PrimitiveObject> void testPrimitives(ResultSetMapperBuilder<T> builder, T object)
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
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.getBoolean("p_boolean")).thenReturn(true);
		when(rs.getByte("p_byte")).thenReturn((byte)0xa3);
		when(rs.getInt("p_character")).thenReturn(0xa4);
		when(rs.getShort("p_short")).thenReturn((short)0xa5);
		when(rs.getInt("p_int")).thenReturn(0xa6);
		when(rs.getLong("p_long")).thenReturn(0xffa4l);
		when(rs.getFloat("p_float")).thenReturn(3.14f);
		when(rs.getDouble("p_double")).thenReturn(3.14159);
		
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
	
	private void testDbObjectMappingFromDb(Mapper<ResultSet, DbObject> mapper)
			throws SQLException, Exception, ParseException {
		DbObject dbObject = new DbObject();
		
		Connection conn = DbHelper.objectDb();
		
		try {
			PreparedStatement ps = conn.prepareStatement("select id, name, email, creation_time from test_db_object where id = 1 ");
			
			try {
				ResultSet rs = ps.executeQuery();
				
				rs.next();
				
				mapper.map(rs, dbObject);
				
				assertEquals(1, dbObject.getId());
				assertEquals("name 1", dbObject.getName());
				assertEquals("name1@mail.com", dbObject.getEmail());
				assertEquals(DateHelper.toDate("2014-03-04 11:10:03"), dbObject.getCreationTime());
				
			} finally {
				ps.close();
			}
			
		} finally {
			conn.close();
		}
	}
}
