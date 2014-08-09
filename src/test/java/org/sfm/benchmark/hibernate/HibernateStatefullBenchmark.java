package org.sfm.benchmark.hibernate;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sfm.beans.DbObject;
import org.sfm.benchmark.BenchmarkRunner;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.benchmark.SysOutBenchmarkListener;
import org.sfm.jdbc.DbHelper;

public class HibernateStatefullBenchmark implements QueryExecutor {

	private SessionFactory sf;
	private Session session;
	private Query query;
	private ScrollableResults sr;
	
	public HibernateStatefullBenchmark(Connection conn) {
		this(HibernateHelper.getSessionFactory(conn, false));
	}
	public HibernateStatefullBenchmark(SessionFactory sessionFactory) {
		sf = sessionFactory;
	}
	@Override
	public void executeQuery() throws Exception {
		sr = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
	}

	@Override
	public void prepareQuery(int limit) throws Exception {
		session = sf.openSession();
		query = session.createQuery("from DbObject");
		if (limit >= 0) {
			query.setMaxResults(limit);
		}
	}

	@Override
	public void forEach(ForEachListener ql) throws Exception {
		while(sr.next()) {
			DbObject o = (DbObject) sr.get(0);
			ql.object(o);
		}
		sr.close();
	}

	@Override
	public void close() throws Exception {
		session.close();
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException, Exception {
		new BenchmarkRunner(-1, new HibernateStatefullBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(HibernateStatefullBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new HibernateStatefullBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(HibernateStatefullBenchmark.class, "SmallQuery"));
	}
}
