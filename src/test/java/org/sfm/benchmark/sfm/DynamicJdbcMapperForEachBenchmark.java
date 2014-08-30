package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.AllBenchmark;
import org.sfm.benchmark.BenchmarkConstants;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.JdbcMapperFactory;

public class DynamicJdbcMapperForEachBenchmark<T> extends ForEachMapperQueryExecutor<T> implements QueryExecutor {
	public DynamicJdbcMapperForEachBenchmark(Connection conn, Class<T> target)
			throws NoSuchMethodException, SecurityException, SQLException {
		super(JdbcMapperFactory.newInstance().newMapper(target), conn, target);
	}
	
	public static void main(String[] args) throws SQLException, Exception {
		AllBenchmark.runBenchmark(DbHelper.mockDb(), SmallBenchmarkObject.class, DynamicJdbcMapperForEachBenchmark.class, BenchmarkConstants.SINGLE_QUERY_SIZE, BenchmarkConstants.SINGLE_NB_ITERATION);
	}
}
