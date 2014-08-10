package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.BenchmarkRunner;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.benchmark.SysOutBenchmarkListener;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.JdbcMapperFactory;

public class DynamicJdbcMapperForEachBenchmark<T> extends ForEachMapperQueryExecutor<T> implements QueryExecutor {
	public DynamicJdbcMapperForEachBenchmark(Connection conn, Class<T> target)
			throws NoSuchMethodException, SecurityException, SQLException {
		super(JdbcMapperFactory.newInstance().newMapper(target), conn, target);
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException, Exception {
		new BenchmarkRunner(-1, new DynamicJdbcMapperForEachBenchmark<SmallBenchmarkObject>(DbHelper.benchmarkDb(), SmallBenchmarkObject.class)).run(new SysOutBenchmarkListener(DynamicJdbcMapperForEachBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new DynamicJdbcMapperForEachBenchmark<SmallBenchmarkObject>(DbHelper.benchmarkDb(), SmallBenchmarkObject.class)).run(new SysOutBenchmarkListener(DynamicJdbcMapperForEachBenchmark.class, "SmallQuery"));
	}
}
