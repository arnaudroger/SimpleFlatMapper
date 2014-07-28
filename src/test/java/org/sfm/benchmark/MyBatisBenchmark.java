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
BigSelect elapsed 5401270000 1000000 5401
BigSelect elapsed 2332615000 1000000 2332
BigSelect elapsed 2262635000 1000000 2262
BigSelect elapsed 2275135000 1000000 2275
BigSelect elapsed 2269317000 1000000 2269
BigSelect elapsed 2258988000 1000000 2258
BigSelect elapsed 2264932000 1000000 2264
BigSelect elapsed 2235428000 1000000 2235
BigSelect elapsed 2235921000 1000000 2235
BigSelect elapsed 2220349000 1000000 2220
BigSelect elapsed 2238349000 1000000 2238
BigSelect elapsed 2234108000 1000000 2234
BigSelect elapsed 2218149000 1000000 2218
BigSelect elapsed 2240846000 1000000 2240
BigSelect elapsed 2244786000 1000000 2244
BigSelect elapsed 2196986000 1000000 2196
BigSelect elapsed 2216458000 1000000 2216
BigSelect elapsed 2190118000 1000000 2190
BigSelect elapsed 2213535000 1000000 2213
BigSelect elapsed 2217965000 1000000 2217
SmallSelect elapsed 27675387000 1000000 27675
SmallSelect elapsed 25653420000 1000000 25653
SmallSelect elapsed 25590153000 1000000 25590
SmallSelect elapsed 25715064000 1000000 25715
SmallSelect elapsed 25695426000 1000000 25695
SmallSelect elapsed 25584124000 1000000 25584
SmallSelect elapsed 25730323000 1000000 25730
SmallSelect elapsed 25734536000 1000000 25734
SmallSelect elapsed 25573562000 1000000 25573
SmallSelect elapsed 25539343000 1000000 25539
SmallSelect elapsed 25597049000 1000000 25597
SmallSelect elapsed 25648332000 1000000 25648
SmallSelect elapsed 25985429000 1000000 25985
SmallSelect elapsed 26078091000 1000000 26078
SmallSelect elapsed 25599357000 1000000 25599
SmallSelect elapsed 25754448000 1000000 25754
SmallSelect elapsed 25561376000 1000000 25561
SmallSelect elapsed 25742873000 1000000 25742
SmallSelect elapsed 25704631000 1000000 25704
SmallSelect elapsed 25563578000 1000000 25563

 */
public class MyBatisBenchmark {


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
	public MyBatisBenchmark(SqlSessionFactory sqlSessionFactory)  {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	private void runBigSelect() throws Exception {
		
		Connection conn = DbHelper.benchmarkDb();
		
		SqlSession session = sqlSessionFactory.openSession(conn);

		
		ValidateHandler handler = new ValidateHandler();
		
		
		long start = System.nanoTime();
		
		session.select("selectDbObjects", handler );
		long elapsed = System.nanoTime() - start;
		
		long c= handler.c;
		
		System.out.println("BigSelect elapsed " + elapsed + " " + c + " " + (elapsed / c));
		
	}
	
	
	private void runSmallSelect() throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		
	SqlSession session = sqlSessionFactory.openSession(conn);

		
		ValidateHandler handler = new ValidateHandler();

		
		long start = System.nanoTime();

		for(int i = 0; i < 1000000; i++) {
			session.select("selectOneDbObjects", handler );
		}
		long elapsed = System.nanoTime() - start;
		
		System.out.println("SmallSelect elapsed " + elapsed + " " + handler.c + " " + (elapsed / handler.c));
		
	}
	
	public static void main(String[] args) throws Exception {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, new SingleConnectionDataSource(DbHelper.benchmarkDb()));
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(DbObjectMapper.class);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		
		MyBatisBenchmark benchmark = new MyBatisBenchmark(sqlSessionFactory);
		
		for(int i = 0; i < 20; i++) {
			benchmark.runBigSelect();
		}
		
		for(int i = 0; i < 20; i++) {
			benchmark.runSmallSelect();
		}
	}


}
