package org.sfm.benchmark;

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
import org.junit.Assert;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;

public class MyBatisBigSelectBenchmark {


	public class ValidateHandler implements ResultHandler {

		public long c;

		@Override
		public void handleResult(ResultContext rc) {
			
			DbObject o = (DbObject) rc.getResultObject();
			
			Assert.assertNotNull(o);
			c++;
			
		}

	}

	private SqlSessionFactory sqlSessionFactory;
	public MyBatisBigSelectBenchmark(SqlSessionFactory sqlSessionFactory)  {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	private void run() throws Exception {
		
		Connection conn = DbHelper.benchmarkDb();
		
		SqlSession session = sqlSessionFactory.openSession(conn);

		
		ValidateHandler handler = new ValidateHandler();
		
		
		long start = System.nanoTime();
		
		session.select("selectDbObjects", handler );
		long elapsed = System.nanoTime() - start;
		
		long c= handler.c;
		
		System.out.println("elapased " + elapsed + " " + c + " " + (elapsed / c));
		
	}
	
	public static void main(String[] args) throws Exception {
		
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, new SingleConnectionDataSource(DbHelper.benchmarkDb()));
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(DbObjectMapper.class);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		
		MyBatisBigSelectBenchmark benchmark = new MyBatisBigSelectBenchmark(sqlSessionFactory);
		for(int i = 0; i < 20; i++) {
			benchmark.run();
		}
	}


}
