package org.sfm.benchmark;

import java.io.IOException;
import java.io.PrintStream;
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

		printHeader(System.out);
		for (int j = 0; j < classes.length; j++) {
			Class<? extends QueryExecutor> benchmark = classes[j];
			int nbIteration = 100000;
			if (j ==0) {
				for (int querySize = 10; querySize <= 10000; querySize *= 10) {
					runBenchmark(conn, target, benchmark, querySize, nbIteration, new BenchmarkListener() {
						@Override
						public void results(long nb, long elapsed) {
						}
					});
				}
				
			}
			for (int querySize = 10; querySize <= 10000; querySize *= 10) {
				System.gc();
				Thread.sleep(200);
				runBenchmark(conn, target, benchmark, querySize, nbIteration);
			}
		}

	}

	public static void runBenchmark(Connection conn,
			Class<SmallBenchmarkObject> objectClassToMap,
			Class<? extends QueryExecutor> benchmark, int querySize, int nbIteration)
			throws Exception {
		CollectBenchmarkListener cbl = new CollectBenchmarkListener();
		runBenchmark(conn, objectClassToMap, benchmark, querySize, nbIteration, cbl);
		output(benchmark.getSimpleName()+"," + objectClassToMap.getSimpleName() + "," + nf2.format(querySize), cbl, System.out);
	}

	public static void printHeader(PrintStream ps) throws IOException {
		ps.println("benchmark,object,query size,10%,25%,50%,90%,95%,99%,99.9%,99.99%,min,avg,max");
	}
	
	static NumberFormat nf = new DecimalFormat("00000.00");
	static NumberFormat nf2 = new DecimalFormat("00000");
	public static void output(String string, CollectBenchmarkListener cbl, PrintStream out) {
		out.print(string);
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(10.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(25.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(50.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(90.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(95.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(99.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(99.9)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(99.99)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getMinValue()));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getMean()));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getMaxValue()));
		out.println();
	}

	private static void runBenchmark(Connection conn,
			Class<SmallBenchmarkObject> target,
			Class<? extends QueryExecutor> benchmark, int querySize, int nbIteration, BenchmarkListener bl)
			throws Exception {
		Constructor<? extends QueryExecutor> c = benchmark.getDeclaredConstructor(Connection.class, Class.class);
		QueryExecutor qe = c.newInstance(conn, target);
		new BenchmarkRunner(nbIteration, querySize, qe).run(bl);

	}
}
