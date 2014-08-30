package org.sfm.jdbc;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.mockdb.MockConnection;
import org.sfm.utils.DateHelper;
import org.sfm.utils.Handler;

public class DbHelper {
	
	private static final int NB_BENCHMARK_OBJECT = 10000;
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
	
	public static Connection getConnection(String[] args) throws SQLException {
		if (args.length > 0) {
			if ("mysql".equals(args[0])) {
				return benchmarkMysqlDb();
			} else if("mock".equals(args[0])) {
				return mockDb();
			}
		}
		
		return benchmarkHsqlDb();
	}
	public static Connection benchmarkHsqlDb() throws SQLException {
		//Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:benchmarkdb", "SA", "");
		Connection c = newHsqlDbConnection();

		createTableAndInsertData(c);
	
		return c;
	}
	
	public static Connection mockDb() throws SQLException {
		return new MockConnection();
	}
	
	public static Connection benchmarkMysqlDb() throws SQLException {
		//Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:benchmarkdb", "SA", "");
		Connection c = newMysqlDbConnection();

		createTableAndInsertData(c);
	
		return c;
	}

	private static void createTableAndInsertData(Connection c)
			throws SQLException {
		Statement st = c.createStatement();
		
		try {
			try {
				ResultSet rs = st.executeQuery("select count(*) from test_small_benchmark_object");
				rs.next();
				if (rs.getLong(1) == NB_BENCHMARK_OBJECT) {
					return;
				}
			}catch(Exception e) {
				// ignore
			}
			
			createSmallBenchmarkObject(st);

			PreparedStatement ps = c.prepareStatement("insert into test_small_benchmark_object values(?, ?, ?, ?)");
			for(int i = 0; i < NB_BENCHMARK_OBJECT; i++) {
				ps.setLong(1, i);
				ps.setString(2, "name " + i);
				ps.setString(3, "name" + i + "@gmail.com");
				ps.setInt(4, 2000 + (i % 14));
				ps.execute();
			}
			

		} finally {
			st.close();
		}
	}
	private static void createDbObject(Statement st) throws SQLException {
		st.execute("create table test_db_object("
				+ " id bigint not null primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time timestamp, type_ordinal int, type_name varchar(10)  )");
	}
	
	private static void createSmallBenchmarkObject(Statement st) throws SQLException {
		st.execute("create table test_small_benchmark_object("
				+ " id bigint not null primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " year_started int  )");
	}
	
	private static Connection newHsqlDbConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
	}
	
	private static Connection newMysqlDbConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://localhost/sfm", "sfm", "");
	}
	
	public static void testDbObjectFromDb(Handler<PreparedStatement> handler )
			throws SQLException, Exception, ParseException {
		
		Connection conn = DbHelper.objectDb();
		
		try {
			PreparedStatement ps = conn.prepareStatement("select id, name, email, creation_time, type_ordinal, type_name from TEST_DB_OBJECT where id = 1 ");
			
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
