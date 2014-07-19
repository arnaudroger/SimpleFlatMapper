package org.flatmap.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.flatmap.beans.DbObject;
import org.flatmap.map.Mapper;
import org.flatmap.utils.DateHelper;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResultSetMapperBuilderTest {

	@Test
	public void testSelectWithManualDefinition() throws Exception {
		
		ResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilder<DbObject>(DbObject.class);
		
		builder.addMapping("id", "id");
		builder.addMapping("name", "name");
		builder.addMapping("email", "email");
		builder.addMapping("creationTime", "creation_time");
		
		Mapper<ResultSet, DbObject> mapper = builder.mapper();
		
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
