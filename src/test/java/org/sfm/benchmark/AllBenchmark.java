package org.sfm.benchmark;

import java.lang.reflect.Constructor;
import java.sql.Connection;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.hibernate.HibernateStatefullBenchmark;
import org.sfm.benchmark.ibatis.MyBatisBenchmark;
import org.sfm.benchmark.sfm.DynamicJdbcMapperForEachBenchmark;
import org.sfm.benchmark.sfm.StaticJdbcMapperBenchmark;
import org.sfm.jdbc.DbHelper;

public class AllBenchmark {
	public static void main(String args[]) throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		Class<SmallBenchmarkObject> target = SmallBenchmarkObject.class;
		
		runBenchmark(conn, target, PureJdbcBenchmark.class);
		runBenchmark(conn, target, StaticJdbcMapperBenchmark.class);
		runBenchmark(conn, target, DynamicJdbcMapperForEachBenchmark.class);
		runBenchmark(conn, target, HibernateStatefullBenchmark.class);
		runBenchmark(conn, target, MyBatisBenchmark.class);

	}

	private static void runBenchmark(Connection conn,
			Class<SmallBenchmarkObject> target,
			Class<? extends QueryExecutor> benchmark) throws Exception {
		
		Constructor<? extends QueryExecutor> c = benchmark.getDeclaredConstructor(Connection.class, Class.class);
		
		
		QueryExecutor qe = c.newInstance(conn, target);
		
		new BenchmarkRunner(-1, qe).run(new SysOutBenchmarkListener(benchmark, "1MQuery"));
		new BenchmarkRunner(1, qe).run(new SysOutBenchmarkListener(benchmark, "1Query"));
		new BenchmarkRunner(10, qe).run(new SysOutBenchmarkListener(benchmark, "10Query"));
		new BenchmarkRunner(1000, qe).run(new SysOutBenchmarkListener(benchmark, "1KQuery"));
		
	}
}
