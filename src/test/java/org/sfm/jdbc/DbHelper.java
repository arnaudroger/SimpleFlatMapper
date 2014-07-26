package org.sfm.jdbc;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.sfm.beans.DbObject;
import org.sfm.utils.DateHelper;
import org.sfm.utils.Handler;

public class DbHelper {
	
	private static boolean objectDb;
	public static Connection objectDb() throws SQLException {
		Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
		
		if (!objectDb) {
			Statement st = c.createStatement();
			
			try {
				st.execute("create table test_db_object("
						+ " id bigint not null primary key,"
						+ " name varchar(100), "
						+ " email varchar(100),"
						+ " creation_Time datetime  )");

				st.execute("insert into test_db_object values(1, 'name 1', 'name1@mail.com', TIMESTAMP'2014-03-04 11:10:03')");
			} finally {
				st.close();
			}
		}
	
		
		objectDb = true;
		return c;
	}
	public static void assertDbObjectMapping(DbObject dbObject) throws ParseException {
		assertEquals(1, dbObject.getId());
		assertEquals("name 1", dbObject.getName());
		assertEquals("name1@mail.com", dbObject.getEmail());
		assertEquals(DateHelper.toDate("2014-03-04 11:10:03"), dbObject.getCreationTime());
	}
	
	
	public static void testDbObjectFromDb(Handler<PreparedStatement> handler )
			throws SQLException, Exception, ParseException {
		
		Connection conn = DbHelper.objectDb();
		
		try {
			PreparedStatement ps = conn.prepareStatement("select id, name, email, creation_time from test_db_object where id = 1 ");
			
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
