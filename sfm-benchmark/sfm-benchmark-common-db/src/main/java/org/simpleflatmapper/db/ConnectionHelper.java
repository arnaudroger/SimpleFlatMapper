package org.simpleflatmapper.db;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.simpleflatmapper.mockdb.MockDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class ConnectionHelper {
	
	private static final int NB_BENCHMARK_OBJECT = 1000;
	public static final String TEST_SMALL_BENCHMARK_OBJECT = "TEST_SMALL_BENCHMARK_OBJECT";

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

	public static void createTableAndInsertData(Connection c)
			throws SQLException {
		c.setReadOnly(false);
		Statement st = c.createStatement();
		
		try {
			try {
				ResultSet rs = st.executeQuery("select count(*) from " + TEST_SMALL_BENCHMARK_OBJECT);
				rs.next();
				if (rs.getLong(1) == NB_BENCHMARK_OBJECT) {
					return;
				} else {
					st.execute("delete from " + TEST_SMALL_BENCHMARK_OBJECT);
				}
			}catch(Exception e) {
				// ignore
				createSmallBenchmarkObject(st);
			}
			

			PreparedStatement ps = c.prepareStatement("insert into " + TEST_SMALL_BENCHMARK_OBJECT + " values(?, ?, ?, ?)");
			for(int i = 0; i < NB_BENCHMARK_OBJECT; i++) {
				ps.setLong(1, i);
				ps.setString(2, "name " + i);
				ps.setString(3, "name" + i + "@gmail.com");
				ps.setInt(4, 2000 + (i % 14));
				ps.addBatch();
				
			}
			
			ps.executeBatch();

		} finally {
			st.close();
			c.setReadOnly(true);
		}
	}
	
	public static void createSmallBenchmarkObject(Statement st) throws SQLException {
		st.execute("create table " + TEST_SMALL_BENCHMARK_OBJECT + "("
				+ " id bigint not null primary key,"
				+ " name varchar(100), "
				+ " email varchar(100),"
				+ " year_started int  )");
	}
	


	
}
