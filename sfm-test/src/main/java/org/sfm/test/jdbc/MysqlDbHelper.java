package org.sfm.test.jdbc;


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
	}


	private static Connection newMysqlDbConnection() throws SQLException {
		String user = "sfm";

		if ("true".equals(System.getenv("TRAVISBUILD"))) {
			user = "travis";
		}

		return DriverManager.getConnection("jdbc:mysql://localhost:3306/sfm", user, null);
	}

	public static void main(String[] args) throws SQLException {
		Connection connection = MysqlDbHelper.objectDb();

		System.out.println("product name  = " + connection.getMetaData().getDatabaseProductName());
		System.out.println("product name  = " + connection.getMetaData().getDatabaseProductVersion());
		System.out.println("product name  = " + connection.getMetaData().getDatabaseMajorVersion());
		System.out.println("product name  = " + connection.getMetaData().getDatabaseMinorVersion());


	}


}
