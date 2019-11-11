package org.simpleflatmapper.test.jdbc;

import org.hsqldb.jdbc.JDBCDataSourceFactory;
import org.junit.Assert;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbObjectWithAlias;
import org.simpleflatmapper.test.beans.DbObjectWithEmptyAlias;
import org.simpleflatmapper.test.beans.DbPartialFinalObject;
import org.simpleflatmapper.test.DateHelper;

import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


public class DbHelper {

	public static final String HSQLDB_URL = "jdbc:hsqldb:mem:mymemdb";
	private static final String HSQLDB_USER = "SA";
	private static final String HSQLDB_PASSWORD = "";

	public static DataSource getHsqlDataSource() throws Exception {
		objectDb();

		Properties props = new Properties();
		props.setProperty("url", HSQLDB_URL);
		props.setProperty("user", HSQLDB_USER);
		props.setProperty("password", HSQLDB_PASSWORD);
		return JDBCDataSourceFactory.createDataSource(props);

	}

	public enum TargetDB {
		HSQLDB, MYSQL, POSTGRESQL
	}
	
	public static final String TEST_DB_OBJECT_QUERY = "select id, name, email, creation_time, type_ordinal, type_name from TEST_DB_OBJECT where id = 1 ";
	private static AtomicBoolean objectDb = new AtomicBoolean();

	public static Connection getDbConnection(TargetDB targetDB) throws SQLException {
		try {
			switch (targetDB) {
				case HSQLDB:
					return objectDb();
				case MYSQL:
					return MysqlDbHelper.objectDb();
				case POSTGRESQL:
					return PostgresDbHelper.objectDb();
			}
		} catch(Exception e) {
			e.printStackTrace();
			// ignore
		}
		return null;
	}

	public static Connection objectDb() throws SQLException {
		Connection c = newHsqlDbConnection();
		
		if (objectDb.compareAndSet(false, true)) {
			Statement st = c.createStatement();
			
			try {
				createDbObject(st);

				st.execute("insert into TEST_DB_OBJECT values(1, 'name 1', 'name1@mail.com', TIMESTAMP'2014-03-04 11:10:03', 2, 'type4')");
				st.execute("insert into TEST_DB_OBJECT values(2, null, null, null, null, null)");

				
				st.execute("create table db_extended_type("
						+ " bytes varbinary(10),"
						+ " url varchar(100), "
						+ " time TIME(6),"
						+ " date DATE,"
						+ " bigdecimal decimal(10,3),"
						+ " biginteger bigint , "
						+ " stringArray VARCHAR(20) ARRAY DEFAULT ARRAY[],"
						+ " stringList VARCHAR(20) ARRAY DEFAULT ARRAY[] )");


				st.execute("create table test_only_key("
						+ " id bigint primary key  )");


				PreparedStatement ps = c.prepareStatement("insert into db_extended_type values (?, 'https://github.com/arnaudroger/SimpleFlatMapper',"
						+ "'07:08:09', '2014-11-02', 123.321, 123, ARRAY [ 'HOT', 'COLD' ], ARRAY [ 'COLD', 'FREEZING' ])");



				try {
					ps.setBytes(1, new byte[] { 'a', 'b', 'c' });
					ps.execute();
				} finally {
					ps.close();
				}

				st.execute("create table issue318("
						+ " id varchar(100),"
						+ " t  timestamp )");

				ps = c.prepareStatement("insert into issue318 values (?, ?)");



				try {
					ps.setString(1, UUID.randomUUID().toString());
					ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
					ps.execute();
				} finally {
					ps.close();
				}

				st.execute("create table issue537(currencyAndAmount_number double, currencyAndAmount_currency  varchar(10) )");
				st.execute("insert into issue537(currencyAndAmount_number, currencyAndAmount_currency) values(100, 'USD')");



				st.execute("create table issue537_b(amount double, currencyCode  varchar(10) )");
				st.execute("insert into issue537_b(amount, currencyCode) values(100, 'USD')");


				st.execute("create table \"labels\"(\"id\" int, \"name\" varchar(100), \"obsolete\" boolean, \"uuid\" uuid)");
				c.commit();
			} finally {
				st.close();
			}
		}
	
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

	public static void assertDbObjectWithEmptyAliasMapping(DbObjectWithEmptyAlias dbObject) throws ParseException  {
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
		return DriverManager.getConnection(HSQLDB_URL, HSQLDB_USER, HSQLDB_PASSWORD);
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
