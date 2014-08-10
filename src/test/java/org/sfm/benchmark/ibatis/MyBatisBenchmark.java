package org.sfm.benchmark.ibatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
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
	private Class<?> target;
	public MyBatisBenchmark(final Connection conn, Class<?> target)  {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Connection connProxy = (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Connection.class} , new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				if (method.getName().equals("close")) {
					return null;
				}
				return method.invoke(conn, args);
			}
		});
		
		Environment environment = new Environment("development", transactionFactory, new SingleConnectionDataSource(connProxy));
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(DbObjectMapper.class);
		this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		this.target = target;

	}
	@Override
	public void forEach(final ForEachListener ql, int limit) throws Exception {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			if (limit != -1) {
				session.select("select" + target.getSimpleName() + "sWithLimit", limit, new ResultHandler() {
					@Override
					public void handleResult(ResultContext arg0) {
						ql.object((DbObject) arg0.getResultObject());
					}
				});
			} else {
				session.select("select" + target.getSimpleName() + "s",new ResultHandler() {
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
		new BenchmarkRunner(-1, new MyBatisBenchmark(DbHelper.benchmarkDb(), DbObject.class)).run(new SysOutBenchmarkListener(MyBatisBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new MyBatisBenchmark(DbHelper.benchmarkDb(), DbObject.class)).run(new SysOutBenchmarkListener(MyBatisBenchmark.class, "SmallQuery"));
	}
}
