package org.simpleflatmapper.test.jdbc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class PostgresDbHelper {
	

	public static Connection objectDb() throws SQLException {
		Connection c = newPostgresDbConnection();
		
		Statement st = c.createStatement();

		try {
			createDbObject(st);

		} finally {
			st.close();
		}

		return c;
	}


	
	private static void createDbObject(Statement st) throws SQLException {

		st.execute("create table IF NOT EXISTS test_only_key("
				+ " id bigint primary key  )");
		st.execute("create table IF NOT EXISTS TEST_DB_OBJECT("
				+ " id bigint primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time timestamp, type_ordinal int, type_name varchar(10)  )");

		try {
			st.execute("CREATE SEQUENCE db_object_seq");
		} catch(Exception e) {
			// ignore
		}
		st.execute("create table IF NOT EXISTS TEST_DB_OBJECT_AUTOINC("
				+ " id bigint primary key DEFAULT nextval('db_object_seq'),"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time timestamp, type_ordinal int, type_name varchar(10)  )");

		st.execute("create table IF NOT EXISTS TEST_DB_OBJECT_CKEY("
				+ " id bigint,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " creation_Time timestamp, type_ordinal int, type_name varchar(10), primary key(id, name)  )");
		st.execute("CREATE TABLE IF NOT EXISTS TEST_UUID\n" +
				"(\n" +
				"  id integer ,\n" +
				"  uid uuid,\n" +
				"  name character varying(255),\n" +
				"  CONSTRAINT pk_id2 PRIMARY KEY (id)\n" +
				")");
		st.execute("TRUNCATE TEST_DB_OBJECT");
		st.execute("TRUNCATE TEST_DB_OBJECT_AUTOINC");
		st.execute("TRUNCATE TEST_DB_OBJECT_CKEY");
		st.execute("TRUNCATE TEST_UUID");
	}


	private static Connection newPostgresDbConnection() throws SQLException {
		String user = null;

		if ("true".equals(System.getenv("TRAVISBUILD"))) {
			user = "postgres";
		}

		return DriverManager.getConnection("jdbc:postgresql://localhost/sfm", user, null);
	}

	public static void main(String[] args) throws SQLException {
		Connection connection = PostgresDbHelper.objectDb();

		System.out.println("product name  = " + connection.getMetaData().getDatabaseProductName());
		System.out.println("product name  = " + connection.getMetaData().getDatabaseProductVersion());
		System.out.println("product name  = " + connection.getMetaData().getDatabaseMajorVersion());
		System.out.println("product name  = " + connection.getMetaData().getDatabaseMinorVersion());

	}


}
