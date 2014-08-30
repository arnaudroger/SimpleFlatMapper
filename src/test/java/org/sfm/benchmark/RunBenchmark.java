package org.sfm.benchmark;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.hibernate.HibernateStatefullBenchmark;
import org.sfm.benchmark.ibatis.MyBatisBenchmark;
import org.sfm.benchmark.sfm.DynamicJdbcMapperForEachBenchmark;
import org.sfm.benchmark.sfm.DynamicNoAsmJdbcMapperForEachBenchmark;
import org.sfm.benchmark.sfm.StaticJdbcMapperBenchmark;
import org.sfm.benchmark.sql2o.Sql2OBenchmark;
import org.sfm.jdbc.DbHelper;

public class RunBenchmark {
	static final int NB_ITERATION = 100000;
	@SuppressWarnings("unchecked")
	public static void main(String args[]) throws Exception {
		
		boolean displayHeader = true;
		int currentArgIndex = 0;
		
		Class<? extends QueryExecutor>[] classes = new Class[] {
				PureJdbcBenchmark.class, 
				StaticJdbcMapperBenchmark.class,
				DynamicJdbcMapperForEachBenchmark.class,
				DynamicNoAsmJdbcMapperForEachBenchmark.class,
				Sql2OBenchmark.class,
				HibernateStatefullBenchmark.class, 
				MyBatisBenchmark.class
				};
		
		if (args.length > currentArgIndex) {
			displayHeader = false;
			String type = args[0].toLowerCase();
			if (type.startsWith("pure")) {
				classes = new Class[] { PureJdbcBenchmark.class };
				currentArgIndex++;
			} else if(type.startsWith("static")) {
				classes = new Class[] { StaticJdbcMapperBenchmark.class };
				currentArgIndex++;
			} else if(type.startsWith("dyn")) {
				if (type.contains("no")) {
					classes = new Class[] { DynamicNoAsmJdbcMapperForEachBenchmark.class };
				} else {
					classes = new Class[] { DynamicJdbcMapperForEachBenchmark.class };
				}
				currentArgIndex++;
			} else if(type.startsWith("sql2o")) {
				classes = new Class[] { Sql2OBenchmark.class };
				currentArgIndex++;
			} else if(type.startsWith("hib")) {
				classes = new Class[] { HibernateStatefullBenchmark.class };
				currentArgIndex++;
			} else if(type.contains("batis")) {
				classes = new Class[] { MyBatisBenchmark.class };
				currentArgIndex++;
			} else if (type.equals("header")) {
				printHeader(System.out);
				return;
			} else {
				System.err.println("Invalid type " + type);
				System.exit(-1);
			}
		}
		Connection conn;
		
		if (args.length > currentArgIndex) {
			conn = DbHelper.getConnection(args[currentArgIndex++]);
		} else {
			conn = DbHelper.benchmarkHsqlDb();
		}
		
		int[] queries = new int[] {1,10,100,1000};
		
		if (args.length > currentArgIndex) {
			String[] sizesStr = args[currentArgIndex++].split(",");
			queries = new int[sizesStr.length];
			for(int i = 0; i < queries.length; i++) {
				queries [i] = Integer.parseInt(sizesStr[i]);
			}
		}
		
		int iteration = NB_ITERATION;
		if (args.length > currentArgIndex) {
			iteration = Integer.parseInt(args[currentArgIndex++]);
		}
		
		Class<SmallBenchmarkObject> target = SmallBenchmarkObject.class;
		System.err.println(conn.getClass().getSimpleName() + " "  + Arrays.toString(queries) + " " + iteration + " " + Arrays.toString(classes));
		if (displayHeader) {
			printHeader(System.out);
		}
		for (int j = 0; j <classes.length; j++) {
			Class<? extends QueryExecutor> benchmark = classes[j];
			for (int querySize : queries) {
				System.gc();
				Thread.sleep(200);
				runBenchmark(conn, target, benchmark, querySize, iteration);
			}
		}

	}
	public static void runBenchmark(Connection conn,
			Class<? extends QueryExecutor> benchmark)
			throws Exception {
		runBenchmark(conn, SmallBenchmarkObject.class, benchmark, BenchmarkConstants.SINGLE_QUERY_SIZE, BenchmarkConstants.SINGLE_NB_ITERATION);
	}
	public static void runBenchmark(Connection conn,
			Class<?> objectClassToMap,
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
			Class<?> target,
			Class<? extends QueryExecutor> benchmark, int querySize, int nbIteration, BenchmarkListener bl)
			throws Exception {
		Constructor<? extends QueryExecutor> c = benchmark.getDeclaredConstructor(Connection.class, Class.class);
		QueryExecutor qe = c.newInstance(conn, target);
		new BenchmarkRunner(nbIteration, querySize, qe).run(bl);

	}
}
