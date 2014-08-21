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
	private static final int MIN_QUERY_SIZE = 10;
	private static final int MAX_QUERY_SIZE = 10000;
	static final int NB_ITERATION = 100000;
	public static void main(String args[]) throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		Class<SmallBenchmarkObject> target = SmallBenchmarkObject.class;

		@SuppressWarnings("unchecked")
		Class<? extends QueryExecutor>[] classes = new Class[] {
				PureJdbcBenchmark.class, StaticJdbcMapperBenchmark.class,
				DynamicJdbcMapperForEachBenchmark.class,
				HibernateStatefullBenchmark.class, MyBatisBenchmark.class
				};

		printHeader(System.out);
		for (int j = 0; j < classes.length; j++) {
			Class<? extends QueryExecutor> benchmark = classes[j];
			for (int querySize = MIN_QUERY_SIZE; querySize <= MAX_QUERY_SIZE; querySize *= 10) {
				System.gc();
				Thread.sleep(200);
				runBenchmark(conn, target, benchmark, querySize, NB_ITERATION);
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
		ps.println("benchmark,object,query size,10%,20%,30%,40%,50%,60%,70%,80%,90%,95%,99%,min,avg,max");
	}
	
	static NumberFormat nf = new DecimalFormat("00000.00");
	static NumberFormat nf2 = new DecimalFormat("00000");
	public static void output(String string, CollectBenchmarkListener cbl, PrintStream out) {
		out.print(string);
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(10.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(20.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(30.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(40.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(50.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(60.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(70.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(80.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(90.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(95.0)));
		out.print(",");
		out.print(nf.format(cbl.getHistogram().getValueAtPercentile(99.0)));
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
