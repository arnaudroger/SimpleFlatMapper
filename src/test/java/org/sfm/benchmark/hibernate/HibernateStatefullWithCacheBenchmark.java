package org.sfm.benchmark.hibernate;

import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.benchmark.BenchmarkRunner;
import org.sfm.benchmark.SysOutBenchmarkListener;
import org.sfm.jdbc.DbHelper;


public class HibernateStatefullWithCacheBenchmark extends HibernateStatefullBenchmark {

	public HibernateStatefullWithCacheBenchmark(Connection conn) {
		super(HibernateHelper.getSessionFactory(conn, true));
	}


	public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException, Exception {
		new BenchmarkRunner(-1, new HibernateStatefullWithCacheBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(HibernateStatefullWithCacheBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new HibernateStatefullWithCacheBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(HibernateStatefullWithCacheBenchmark.class, "SmallQuery"));
	}

}
