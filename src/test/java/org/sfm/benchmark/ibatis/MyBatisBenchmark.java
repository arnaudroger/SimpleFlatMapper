package org.sfm.benchmark.ibatis;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.sfm.beans.DbObject;
import org.sfm.benchmark.BenchmarkRunner;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.benchmark.SingleConnectionDataSource;
import org.sfm.benchmark.SysOutBenchmarkListener;
import org.sfm.jdbc.DbHelper;

public class MyBatisBenchmark implements QueryExecutor {

	private SqlSessionFactory sqlSessionFactory;
	private Connection conn;
	
	public MyBatisBenchmark(Connection conn)  {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, new SingleConnectionDataSource(conn));
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(DbObjectMapper.class);
		this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		this.conn = conn;

	}
	@Override
	public void forEach(final ForEachListener ql, int limit) throws Exception {
		SqlSession session = sqlSessionFactory.openSession(conn);
		try {
			if (limit != -1) {
				session.select("selectDbObjects", new RowBounds(0, limit), new ResultHandler() {
					@Override
					public void handleResult(ResultContext arg0) {
						ql.object((DbObject) arg0.getResultObject());
					}
				});
			} else {
				session.select("selectDbObjects",new ResultHandler() {
					@Override
					public void handleResult(ResultContext arg0) {
						ql.object((DbObject) arg0.getResultObject());
					}
				});
			}
		} finally {
			session.close();
		}
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException, Exception {
		new BenchmarkRunner(-1, new MyBatisBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(MyBatisBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new MyBatisBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(MyBatisBenchmark.class, "SmallQuery"));
	}
}
