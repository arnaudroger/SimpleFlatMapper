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

/*
init db
init db done
elapased 5340710000 1000000 5340
elapased 2421282000 1000000 2421
elapased 2385571000 1000000 2385
elapased 2362732000 1000000 2362
elapased 2360808000 1000000 2360
elapased 2356946000 1000000 2356
elapased 2319661000 1000000 2319
elapased 2659769000 1000000 2659
elapased 2439624000 1000000 2439
elapased 2371551000 1000000 2371
elapased 2614737000 1000000 2614
elapased 2583338000 1000000 2583
elapased 2430983000 1000000 2430
elapased 2466935000 1000000 2466
elapased 2610856000 1000000 2610
elapased 2482380000 1000000 2482
elapased 2505391000 1000000 2505
elapased 2986674000 1000000 2986
elapased 2630663000 1000000 2630
elapased 2494846000 1000000 2494
 */
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
