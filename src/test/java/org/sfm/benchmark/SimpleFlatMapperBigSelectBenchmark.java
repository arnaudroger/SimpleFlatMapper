package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.ResultSetMapperFactory;
import org.sfm.utils.Handler;

public class SimpleFlatMapperBigSelectBenchmark {
	private static final class ValidateHandler implements Handler<DbObject> {
		long c;

		@Override
		public void handle(DbObject t) throws Exception {
			Assert.assertNotNull(t.getName());
			c++;
		}
	}

	JdbcMapper<DbObject> mapper;
	public SimpleFlatMapperBigSelectBenchmark() throws NoSuchMethodException, SecurityException, SQLException {
		mapper = ResultSetMapperFactory.newMapper(DbObject.class);
	}
	
	private void run() throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM test_db_object");
		
		ResultSet rs = ps.executeQuery();
		
		long start = System.nanoTime();
		
		long c = mapper.forEach(rs, new ValidateHandler() ).c;
		long elapsed = System.nanoTime() - start;
		
		System.out.println("elapased " + elapsed + " " + c + " " + (elapsed / c));
		
	}
	
	public static void main(String[] args) throws Exception {
		SimpleFlatMapperBigSelectBenchmark benchmark = new SimpleFlatMapperBigSelectBenchmark();
		for(int i = 0; i < 20; i++) {
			benchmark.run();
		}
	}


}
