package org.simpleflatmapper.test.jdbc;


import java.sql.*;


public class MysqlDbHelper {
	

	public static Connection objectDb() throws SQLException {
		Connection c = newMysqlDbConnection();
		
		Statement st = c.createStatement();

		try {
			createDbObject(st);

		} finally {
			st.close();
		}

		return c;
	}


	
	private static void createDbObject(Statement st) throws SQLException {

		st.execute("create table IF NOT EXISTS TEST_ONLY_KEY("
				+ " id bigint primary key  )");
		st.execute("create table IF NOT EXISTS TEST_DB_OBJECT("
				+ " id bigint primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time DATETIME, type_ordinal int, type_name varchar(10)  )");


		st.execute("create table IF NOT EXISTS TEST_DB_OBJECT_AUTOINC("
				+ " id bigint AUTO_INCREMENT primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time DATETIME, type_ordinal int, type_name varchar(10)  )");

		st.execute("create table IF NOT EXISTS TEST_DB_OBJECT_CKEY("
				+ " id bigint,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time DATETIME, type_ordinal int, type_name varchar(10), primary key(id, name)  )");

		st.execute("create table IF NOT EXISTS TEST_DB_OBJECT_AUTOINC_NAMEINDEX("
				+ " id bigint AUTO_INCREMENT primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time DATETIME, type_ordinal int, type_name varchar(10)  )");
		try {
			st.execute("create unique index nameindex on TEST_DB_OBJECT_AUTOINC_NAMEINDEX(name)");
		} catch(Exception e) {
			// IGNORE
		}
		st.execute("TRUNCATE TEST_DB_OBJECT");
		st.execute("TRUNCATE TEST_DB_OBJECT_AUTOINC");
		st.execute("TRUNCATE TEST_DB_OBJECT_CKEY");
		st.execute("TRUNCATE TEST_DB_OBJECT_AUTOINC_NAMEINDEX");
	}


	private static Connection newMysqlDbConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/sfm?useOldAliasMetadataBehavior=false", "sfm", null);
	}

	public static void main(String[] args) throws SQLException {
		Connection connection = MysqlDbHelper.objectDb();

		System.out.println("product name  = " + connection.getMetaData().getDatabaseProductName());
		System.out.println("product name  = " + connection.getMetaData().getDatabaseProductVersion());
		System.out.println("product name  = " + connection.getMetaData().getDatabaseMajorVersion());
		System.out.println("product name  = " + connection.getMetaData().getDatabaseMinorVersion());


	}


}
