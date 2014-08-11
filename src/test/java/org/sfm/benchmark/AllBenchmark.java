package org.sfm.benchmark;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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

		@SuppressWarnings("unchecked")
		Class<? extends QueryExecutor>[] classes = new Class[] {
				PureJdbcBenchmark.class, StaticJdbcMapperBenchmark.class,
				DynamicJdbcMapperForEachBenchmark.class,
				HibernateStatefullBenchmark.class, MyBatisBenchmark.class };

		System.out.println("benchmark,object,query size,min time per object,median time per object,avg time per object,max time per object");
		for (int j = 0; j < classes.length; j++) {
			Class<? extends QueryExecutor> benchmark = classes[j];
			
			for (int i = 10; i <= 10000; i *= 10) {
				CollectBenchmarkListener cbl = new CollectBenchmarkListener();
				runBenchmark(conn, target, benchmark, i, cbl);
				output(benchmark.getSimpleName()+"," + target.getSimpleName() + "," + nf2.format(i), cbl);
			}
		}

	}
	static NumberFormat nf = new DecimalFormat("00000.00");
	static NumberFormat nf2 = new DecimalFormat("00000");
	private static void output(String string, CollectBenchmarkListener cbl) {
		System.out.print(string);
		System.out.print(",");
		System.out.print(nf.format(cbl.min().timePerObject));
		System.out.print(",");
		System.out.print(nf.format(cbl.median().timePerObject));
		System.out.print(",");
		System.out.print(nf.format(cbl.avg().timePerObject));
		System.out.print(",");
		System.out.print(nf.format(cbl.max().timePerObject));
		System.out.println();
	}

	private static void runBenchmark(Connection conn,
			Class<SmallBenchmarkObject> target,
			Class<? extends QueryExecutor> benchmark, int limit, BenchmarkListener bl)
			throws Exception {
		System.gc();
		Thread.sleep(500);
		
		Constructor<? extends QueryExecutor> c = benchmark.getDeclaredConstructor(Connection.class, Class.class);
		QueryExecutor qe = c.newInstance(conn, target);
		new BenchmarkRunner(limit, qe).run(bl);

	}
}
