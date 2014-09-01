package org.sfm.benchmark.ibatis;

import java.sql.Connection;

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
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.QueryExecutor;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class MyBatisBenchmark implements QueryExecutor {

	private SqlSessionFactory sqlSessionFactory;
	private Class<?> target;
	public MyBatisBenchmark(final Connection conn, Class<?> target)  {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, new SingleConnectionDataSource(conn, true));
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
	
}
