package org.sfm.test.jdbc;

import org.junit.Assert;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbObjectWithAlias;
import org.sfm.beans.DbPartialFinalObject;
import org.sfm.test.DateHelper;

import java.sql.*;
import java.text.ParseException;


public class DbHelper {

	public enum TargetDB {
		HSQLDB, MYSQL, POSTGRESQL
	}
	
	public static final String TEST_DB_OBJECT_QUERY = "select id, name, email, creation_time, type_ordinal, type_name from TEST_DB_OBJECT where id = 1 ";
	private static boolean objectDb;

	public static Connection getDbConnection(TargetDB targetDB) throws SQLException {
		switch (targetDB) {
			case HSQLDB:
				return objectDb();
			case MYSQL:
				return MysqlDbHelper.objectDb();
			case POSTGRESQL:
				return PostgresDbHelper.objectDb();
		}
		throw new IllegalArgumentException();
	}

	public static Connection objectDb() throws SQLException {
		Connection c = newHsqlDbConnection();
		
		if (!objectDb) {
			Statement st = c.createStatement();
			
			try {
				createDbObject(st);

				st.execute("insert into TEST_DB_OBJECT values(1, 'name 1', 'name1@mail.com', TIMESTAMP'2014-03-04 11:10:03', 2, 'type4')");
				
				
				st.execute("create table db_extended_type("
						+ " bytes varbinary(10),"
						+ " url varchar(100), "
						+ " time TIME(6),"
						+ " date DATE,"
						+ " bigdecimal decimal(10,3),"
						+ " biginteger bigint , "
						+ " stringArray VARCHAR(20) ARRAY DEFAULT ARRAY[],"
						+ " stringList VARCHAR(20) ARRAY DEFAULT ARRAY[] )");
				
				PreparedStatement ps = c.prepareStatement("insert into db_extended_type values (?, 'https://github.com/arnaudroger/SimpleFlatMapper',"
						+ "'07:08:09', '2014-11-02', 123.321, 123, ARRAY [ 'HOT', 'COLD' ], ARRAY [ 'COLD', 'FREEZING' ])");
				try {
					ps.setBytes(1, new byte[] { 'a', 'b', 'c' });
					ps.execute();
				} finally {
					ps.close();
				}
				c.commit();
			} finally {
				st.close();
			}
		}
	
		
		objectDb = true;
		return c;
	}

	public static void assertDbObjectMapping(DbObject dbObject) throws ParseException  {
		Assert.assertEquals(1, dbObject.getId());
		Assert.assertEquals("name 1", dbObject.getName());
		Assert.assertEquals("name1@mail.com", dbObject.getEmail());
		Assert.assertEquals(DateHelper.toDate("2014-03-04 11:10:03"), dbObject.getCreationTime());
		Assert.assertEquals(DbObject.Type.type3, dbObject.getTypeOrdinal());
		Assert.assertEquals(DbObject.Type.type4, dbObject.getTypeName());
	}
	
	public static void assertDbObjectWithAliasMapping(DbObjectWithAlias dbObject) throws ParseException  {
		Assert.assertEquals(1, dbObject.getIdWithAlias());
		Assert.assertEquals("name 1", dbObject.getNameWithAlias());
		Assert.assertEquals("name1@mail.com", dbObject.getEmailWithAlias());
		Assert.assertEquals(DateHelper.toDate("2014-03-04 11:10:03"), dbObject.getCreationTimeWithAlias());
		Assert.assertEquals(DbObject.Type.type3, dbObject.getTypeOrdinalWithAlias());
		Assert.assertEquals(DbObject.Type.type4, dbObject.getTypeNameWithAlias());
	}
	public static void assertDbObjectMapping(int i, DbObject dbObject) throws ParseException  {
		Assert.assertEquals(i, dbObject.getId());
		Assert.assertEquals("name " + i, dbObject.getName());
		Assert.assertEquals("name" + i + "@mail.com", dbObject.getEmail());
		Assert.assertEquals(DateHelper.toDate("2014-03-04 11:10:03"), dbObject.getCreationTime());
		Assert.assertEquals(DbObject.Type.type3, dbObject.getTypeOrdinal());
		Assert.assertEquals(DbObject.Type.type4, dbObject.getTypeName());
	}
	public static void assertDbObjectMapping(DbFinalObject dbObject) throws ParseException {
		Assert.assertEquals(1, dbObject.getId());
		Assert.assertEquals("name 1", dbObject.getName());
		Assert.assertEquals("name1@mail.com", dbObject.getEmail());
		Assert.assertEquals(DateHelper.toDate("2014-03-04 11:10:03"), dbObject.getCreationTime());
		Assert.assertEquals(DbObject.Type.type3, dbObject.getTypeOrdinal());
		Assert.assertEquals(DbObject.Type.type4, dbObject.getTypeName());
	}
	public static void assertDbObjectMapping(
			DbPartialFinalObject dbObject) throws ParseException {
		Assert.assertEquals(1, dbObject.getId());
		Assert.assertEquals("name 1", dbObject.getName());
		Assert.assertEquals("name1@mail.com", dbObject.getEmail());
		Assert.assertEquals(DateHelper.toDate("2014-03-04 11:10:03"), dbObject.getCreationTime());
		Assert.assertEquals(DbObject.Type.type3, dbObject.getTypeOrdinal());
		Assert.assertEquals(DbObject.Type.type4, dbObject.getTypeName());
	}
	
	private static void createDbObject(Statement st) throws SQLException {
		st.execute("create table test_db_object("
				+ " id bigint primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time timestamp, type_ordinal int, type_name varchar(10)  )");


		st.execute("create table test_db_object_autoinc("
				+ " id bigint GENERATED BY DEFAULT AS IDENTITY primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time timestamp, type_ordinal int, type_name varchar(10)  )");

		st.execute("create table test_db_object_ckey("
				+ " id bigint,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time timestamp, type_ordinal int, type_name varchar(10), primary key(id, name)  )");
	}


	private static Connection newHsqlDbConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
	}
	
	
	public static void testDbObjectFromDb(TestRowHandler<PreparedStatement> handler )
			throws SQLException, Exception, ParseException {
		
		String query = TEST_DB_OBJECT_QUERY;
		testQuery(handler, query);
	}

	public static void testQuery(TestRowHandler<PreparedStatement> handler,
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
