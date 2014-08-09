package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.benchmark.BenchmarkRunner;
import org.sfm.benchmark.SysOutBenchmarkListener;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.DelegateJdbcMapper;
import org.sfm.jdbc.ResultSetMapperBuilder;
import org.sfm.reflect.Instantiator;

public class StaticJdbcMapperBenchmark extends ForEachMapperQueryExecutor {
	public StaticJdbcMapperBenchmark(Connection conn) throws NoSuchMethodException, SecurityException, SQLException {
		super(new DelegateJdbcMapper<DbObject>(new ResultSetMapperBuilder<DbObject>(DbObject.class).addIndexedColumn("id").addIndexedColumn("name").addIndexedColumn("email").addIndexedColumn("creation_time").mapper(), 
				new Instantiator<DbObject>() {
					@Override
					public DbObject newInstance() throws Exception {
						return new DbObject();
					}
				}), conn);
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException, Exception {
		new BenchmarkRunner(-1, new StaticJdbcMapperBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(StaticJdbcMapperBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new StaticJdbcMapperBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(StaticJdbcMapperBenchmark.class, "SmallQuery"));
	}

}
