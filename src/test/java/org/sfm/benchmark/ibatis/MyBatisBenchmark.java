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
	private SqlSession session;
	private int limit;
	
	public MyBatisBenchmark(Connection conn)  {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, new SingleConnectionDataSource(conn));
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(DbObjectMapper.class);
		this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

	}
	@Override
	public void executeQuery() throws Exception {
	}
	@Override
	public void prepareQuery(int limit) throws Exception {
		session = sqlSessionFactory.openSession();
		this.limit = limit;
	}
	@Override
	public void forEach(final ForEachListener ql) throws Exception {
		RowBounds rb = null;
		if (limit != -1) {
			rb = new RowBounds(0, limit);
		}
		session.select("selectDbObjects", null, rb, new ResultHandler() {
			@Override
			public void handleResult(ResultContext arg0) {
				ql.object((DbObject) arg0.getResultObject());
			}
		});
		
	}
	@Override
	public void close() throws Exception {
		session.close();
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException, Exception {
		new BenchmarkRunner(-1, new MyBatisBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(MyBatisBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new MyBatisBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(MyBatisBenchmark.class, "SmallQuery"));
	}

}
