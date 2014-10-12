package org.sfm.jdbc;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.utils.ListHandler;

public class JdbcMapperBuilderTest {

	
	@Test
	public void testWithWrongColumn() throws MappingException, SQLException {
		ResultSetMapperBuilderImpl<DbObject> builder = new ResultSetMapperBuilderImpl<DbObject>(DbObject.class);
		builder.mapperBuilderErrorHandler(MapperBuilderErrorHandler.NULL);
		builder.addIndexedColumn("no_id").addIndexedColumn("no_name").addIndexedColumn("email");
		
		JdbcMapper<DbObject> mapper = builder.mapper();
		
		List<DbObject> l = mapper.forEach(new MockDbObjectResultSet(1), new ListHandler<DbObject>()).getList();
		
		assertEquals(1, l.size());
		assertEquals(0, l.get(0).getId());
		assertNull(l.get(0).getName());
		assertEquals("email1", l.get(0).getEmail());
	}
}
