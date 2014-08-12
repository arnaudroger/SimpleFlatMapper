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

		System.out.println("benchmark,object,query size,10%,25%,50%,90%,95%,99%,99.9%,99.99%,min,avg,max");
		for (int j = 0; j < classes.length; j++) {
			Class<? extends QueryExecutor> benchmark = classes[j];
			
			for (int i = 10; i <= 10000; i *= 10) {
				System.gc();
				Thread.sleep(200);
				CollectBenchmarkListener cbl = new CollectBenchmarkListener();
				runBenchmark(conn, target, benchmark, i, cbl);
				output(benchmark.getSimpleName()+"," + target.getSimpleName() + "," + nf2.format(i), cbl, i);
			}
		}

	}
	static NumberFormat nf = new DecimalFormat("00000.00");
	static NumberFormat nf2 = new DecimalFormat("00000");
	private static void output(String string, CollectBenchmarkListener cbl, int nbObject) {
		System.out.print(string);
		System.out.print(",");
		System.out.print(nf.format(cbl.getHistogram().getValueAtPercentile(10.0)));
		System.out.print(",");
		System.out.print(nf.format(cbl.getHistogram().getValueAtPercentile(25.0)));
		System.out.print(",");
		System.out.print(nf.format(cbl.getHistogram().getValueAtPercentile(50.0)));
		System.out.print(",");
		System.out.print(nf.format(cbl.getHistogram().getValueAtPercentile(90.0)));
		System.out.print(",");
		System.out.print(nf.format(cbl.getHistogram().getValueAtPercentile(95.0)));
		System.out.print(",");
		System.out.print(nf.format(cbl.getHistogram().getValueAtPercentile(99.0)));
		System.out.print(",");
		System.out.print(nf.format(cbl.getHistogram().getValueAtPercentile(99.9)));
		System.out.print(",");
		System.out.print(nf.format(cbl.getHistogram().getValueAtPercentile(99.99)));
		System.out.print(",");
		System.out.print(nf.format(cbl.getHistogram().getMinValue()));
		System.out.print(",");
		System.out.print(nf.format(cbl.getHistogram().getMean()));
		System.out.print(",");
		System.out.print(nf.format(cbl.getHistogram().getMaxValue()));
		System.out.println();
	}

	private static void runBenchmark(Connection conn,
			Class<SmallBenchmarkObject> target,
			Class<? extends QueryExecutor> benchmark, int limit, BenchmarkListener bl)
			throws Exception {
		Constructor<? extends QueryExecutor> c = benchmark.getDeclaredConstructor(Connection.class, Class.class);
		QueryExecutor qe = c.newInstance(conn, target);
		new BenchmarkRunner(10000, limit, qe).run(bl);

	}
}
