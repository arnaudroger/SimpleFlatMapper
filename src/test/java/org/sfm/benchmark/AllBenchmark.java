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
		
		int[] limits = new int[] { 1000, 10000, 100000, -1, 100, 10, 1}; 
		for(int i = 0; i < limits.length; i++) {
			int limit = limits[i];
			runBenchmark(conn, target, PureJdbcBenchmark.class, limit);
			runBenchmark(conn, target, StaticJdbcMapperBenchmark.class, limit);
			runBenchmark(conn, target, DynamicJdbcMapperForEachBenchmark.class, limit);
			runBenchmark(conn, target, HibernateStatefullBenchmark.class, limit);
			runBenchmark(conn, target, MyBatisBenchmark.class, limit);
		}

	}

	private static void runBenchmark(Connection conn,
			Class<SmallBenchmarkObject> target,
			Class<? extends QueryExecutor> benchmark, int limit) throws Exception {
		
		Constructor<? extends QueryExecutor> c = benchmark.getDeclaredConstructor(Connection.class, Class.class);
		
		QueryExecutor qe = c.newInstance(conn, target);
		String name ;
		switch (limit) {
		case 1000:
			name = "1K";
			break;
		case 10000:
			name = "10K";
			break;
		case 100000:
			name = "100K";
			break;
		case 1000000:
			name = "1M";
			break;
		default:
			name = Integer.toString(limit);
		}
		new BenchmarkRunner(limit, qe).run(new SysOutBenchmarkListener(benchmark, name));
		
	}
}
