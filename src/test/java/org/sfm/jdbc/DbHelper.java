package org.sfm.jdbc;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.utils.DateHelper;
import org.sfm.utils.RowHandler;

public class DbHelper {
	
	public static final String TEST_DB_OBJECT_QUERY = "select id, name, email, creation_time, type_ordinal, type_name from TEST_DB_OBJECT where id = 1 ";
	private static boolean objectDb;
	
	public static Connection objectDb() throws SQLException {
		Connection c = newHsqlDbConnection();
		
		if (!objectDb) {
			Statement st = c.createStatement();
			
			try {
				createDbObject(st);

				st.execute("insert into TEST_DB_OBJECT values(1, 'name 1', 'name1@mail.com', TIMESTAMP'2014-03-04 11:10:03', 2, 'type4')");
				c.commit();
			} finally {
				st.close();
			}
		}
	
		
		objectDb = true;
		return c;
	}

	public static void assertDbObjectMapping(DbObject dbObject) throws ParseException  {
		assertEquals(1, dbObject.getId());
		assertEquals("name 1", dbObject.getName());
		assertEquals("name1@mail.com", dbObject.getEmail());
		assertEquals(DateHelper.toDate("2014-03-04 11:10:03"), dbObject.getCreationTime());
		assertEquals(DbObject.Type.type3, dbObject.getTypeOrdinal());
		assertEquals(DbObject.Type.type4, dbObject.getTypeName());
	}
	
	public static void assertDbObjectMapping(DbFinalObject dbObject) throws ParseException {
		assertEquals(1, dbObject.getId());
		assertEquals("name 1", dbObject.getName());
		assertEquals("name1@mail.com", dbObject.getEmail());
		assertEquals(DateHelper.toDate("2014-03-04 11:10:03"), dbObject.getCreationTime());
		assertEquals(DbObject.Type.type3, dbObject.getTypeOrdinal());
		assertEquals(DbObject.Type.type4, dbObject.getTypeName());
	}
	
	
	private static void createDbObject(Statement st) throws SQLException {
		st.execute("create table test_db_object("
				+ " id bigint not null primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time timestamp, type_ordinal int, type_name varchar(10)  )");
	}
	
	
	private static Connection newHsqlDbConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
	}
	
	
	public static void testDbObjectFromDb(RowHandler<PreparedStatement> handler )
			throws SQLException, Exception, ParseException {
		
		String query = TEST_DB_OBJECT_QUERY;
		testQuery(handler, query);
	}

	public static void testQuery(RowHandler<PreparedStatement> handler,
			String query) throws SQLException, Exception {
		Connection conn = DbHelper.objectDb();
		
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			
			try {
				handler.handle(ps);
			} finally {
				ps.close();
			}
			
		} finally {
			conn.close();
		}
	}
}
