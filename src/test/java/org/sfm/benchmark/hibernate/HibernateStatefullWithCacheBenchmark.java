package org.sfm.benchmark.hibernate;

import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.benchmark.BenchmarkRunner;
import org.sfm.benchmark.SysOutBenchmarkListener;
import org.sfm.jdbc.DbHelper;


public class HibernateStatefullWithCacheBenchmark extends HibernateStatefullBenchmark {

	public HibernateStatefullWithCacheBenchmark(Connection conn, Class<?> target) {
		super(HibernateHelper.getSessionFactory(conn, true), target);
	}


	public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException, Exception {
		new BenchmarkRunner(-1, new HibernateStatefullWithCacheBenchmark(DbHelper.benchmarkDb(), DbObject.class)).run(new SysOutBenchmarkListener(HibernateStatefullWithCacheBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new HibernateStatefullWithCacheBenchmark(DbHelper.benchmarkDb(), DbObject.class)).run(new SysOutBenchmarkListener(HibernateStatefullWithCacheBenchmark.class, "SmallQuery"));
	}

}
