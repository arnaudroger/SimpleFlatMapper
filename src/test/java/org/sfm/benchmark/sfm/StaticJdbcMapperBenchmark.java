package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.AllBenchmark;
import org.sfm.benchmark.BenchmarkConstants;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.ResultSetMapperBuilderImpl;

public class StaticJdbcMapperBenchmark<T> extends ForEachMapperQueryExecutor<T> {
	public StaticJdbcMapperBenchmark(Connection conn, Class<T> target) throws NoSuchMethodException, SecurityException, SQLException {
		super(newMapper(target), conn, target);
	}

	@SuppressWarnings("unchecked")
	private static <T> JdbcMapper<T> newMapper(Class<T> target) throws NoSuchMethodException, SecurityException {
		if (target.equals(DbObject.class)) {
			return (JdbcMapper<T>) 
							new ResultSetMapperBuilderImpl<DbObject>(DbObject.class)
								.addIndexedColumn("id")
								.addIndexedColumn("name")
								.addIndexedColumn("email")
								.addIndexedColumn("creation_time").mapper();
		} else if (target.equals(SmallBenchmarkObject.class)) {
			return (JdbcMapper<T>) 
							new ResultSetMapperBuilderImpl<SmallBenchmarkObject>(SmallBenchmarkObject.class)
								.addIndexedColumn("id")
								.addIndexedColumn("name")
								.addIndexedColumn("email")
								.addIndexedColumn("year_started").mapper();
		} else {
			throw new UnsupportedOperationException(target.getName());
		}
	}
	
	public static void main(String[] args) throws SQLException, Exception {
		AllBenchmark.runBenchmark(DbHelper.benchmarkHsqlDb(), SmallBenchmarkObject.class, StaticJdbcMapperBenchmark.class, BenchmarkConstants.SINGLE_QUERY_SIZE, BenchmarkConstants.SINGLE_NB_ITERATION);
	}

}
