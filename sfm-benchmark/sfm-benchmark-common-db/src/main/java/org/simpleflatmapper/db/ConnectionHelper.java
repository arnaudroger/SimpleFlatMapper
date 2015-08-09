package org.simpleflatmapper.db;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.simpleflatmapper.mockdb.MockDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class ConnectionHelper {
	
	private static final int NB_BENCHMARK_OBJECT = 1000;
	public static final String TEST_SMALL_BENCHMARK_OBJECT = "TEST_SMALL_BENCHMARK_OBJECT";
	public static final String TEST_BENCHMARK_OBJECT_16 = "TEST_BENCHMARK_OBJECT_16";

	public enum Table {
		SMALL, BIG;

		public String toSqlName() {
			switch (this) {
				case SMALL: return TEST_SMALL_BENCHMARK_OBJECT;
				case BIG: return TEST_BENCHMARK_OBJECT_16;
			}
			return null;
		}

		public int nbFields() {
			switch (this) {
				case SMALL: return 4;
				case BIG: return 16;
			}
			return 0;
		}
	}
	public static DataSource getDataSource(DbTarget db) {
		switch (db) {
		case MOCK:
			return new MockDataSource();
		case HSQLDB:
			return hsqlDbDataSource();
		case MYSQL:
			return mysqlDataSource();
		case H2:
			return h2DbDataSource();
		}
		throw new IllegalArgumentException("Invalid db " + db);
	}

	private static DataSource mysqlDataSource() {
		HikariConfig config = new HikariConfig();
		
		config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		config.addDataSourceProperty("user", "sfm");
		//config.addDataSourceProperty("password", "");
		config.addDataSourceProperty("url", "jdbc:mysql://localhost/sfm?useServerPrepStmts=true");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "10");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		
		return new HikariDataSource(config);
	}

	private static DataSource hsqlDbDataSource() {
		HikariConfig config = new HikariConfig();
		
		config.setDataSourceClassName("org.hsqldb.jdbc.JDBCDataSource");
		config.addDataSourceProperty("url", "jdbc:hsqldb:mem:mymemdb");
		config.setUsername("SA");
		config.setPassword("");
		
		return new HikariDataSource(config);
	}

	private static DataSource h2DbDataSource() {
		HikariConfig config = new HikariConfig();

		config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
		config.addDataSourceProperty("url", "jdbc:h2:mem:mymemdb");
		config.setUsername("SA");
		config.setPassword("");

		return new HikariDataSource(config);
	}

	public static void createTableAndInsertData(Connection c, Table table)
			throws SQLException {
		c.setReadOnly(false);
		Statement st = c.createStatement();

		try {
			try {
				ResultSet rs = st.executeQuery("select count(*) from " + table.toSqlName());
				rs.next();
				if (rs.getLong(1) == NB_BENCHMARK_OBJECT) {
					return;
				} else {
					st.execute("delete from " + table.toSqlName());
				}
			}catch(Exception e) {
				// ignore
				switch (table) {
					case SMALL:
					createSmallBenchmarkObject(st);
					case BIG:
						createBigBenchmarkObject(st);
				}
			}
			

			PreparedStatement ps = c.prepareStatement("insert into " + table.toSqlName() + " values(" + questionMark(table) + ")");
			for(int i = 0; i < NB_BENCHMARK_OBJECT; i++) {
				ps.setLong(1, i);
				ps.setString(2, "name " + i);
				ps.setString(3, "name" + i + "@gmail.com");
				ps.setInt(4, 2000 + (i % 14));

				for(int j  = 5; j <= table.nbFields(); j++) {
					ps.setInt(j, j + i);
				}
				ps.addBatch();
				
			}
			
			ps.executeBatch();

		} finally {
			st.close();
			c.setReadOnly(true);
		}
	}

	private static String questionMark(Table table) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < table.nbFields(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append("?");
		}
		return sb.toString();
	}

	public static void createSmallBenchmarkObject(Statement st) throws SQLException {
		st.execute("create table " + TEST_SMALL_BENCHMARK_OBJECT + "("
				+ " id bigint not null primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " year_started int  )");
	}

	public static void createBigBenchmarkObject(Statement st) throws SQLException {
		st.execute("create table " + TEST_BENCHMARK_OBJECT_16 + "("
				+ " id bigint not null primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " year_started int," +
				" field5 SMALLINT," +
				"field6 int," +
				"field7 bigint," +
				"field8 float," +
				"field9 double," +
				" field10 SMALLINT," +
				"field11 int," +
				"field12 bigint," +
				"field13 float," +
				"field14 double," +
				"field15 int," +
				"field16 int" +
				" )");
	}


	
}
