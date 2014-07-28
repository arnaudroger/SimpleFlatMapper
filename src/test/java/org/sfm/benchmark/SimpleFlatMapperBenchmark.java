package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.ResultSetMapperFactory;

/*
 elapased 701595000 1000000 701
elapased 610761000 1000000 610
elapased 541784000 1000000 541
elapased 547786000 1000000 547
elapased 536929000 1000000 536
elapased 543782000 1000000 543
elapased 548968000 1000000 548
elapased 547359000 1000000 547
elapased 469154000 1000000 469
elapased 470288000 1000000 470
elapased 538422000 1000000 538
elapased 533390000 1000000 533
elapased 474490000 1000000 474
elapased 472035000 1000000 472
elapased 533603000 1000000 533
elapased 554950000 1000000 554
elapased 470591000 1000000 470
elapased 1688242000 1000000 1688
elapased 496344000 1000000 496
elapased 474616000 1000000 474
 */
public class SimpleFlatMapperBenchmark {
	JdbcMapper<DbObject> mapper;
	public SimpleFlatMapperBenchmark() throws NoSuchMethodException, SecurityException, SQLException {
		mapper = ResultSetMapperFactory.newMapper(DbObject.class);
	}
	
	private void runBigSelect() throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM test_db_object");
		
		ResultSet rs = ps.executeQuery();
		
		long start = System.nanoTime();
		
		long c = mapper.forEach(rs, new ValidateHandler() ).c;
		long elapsed = System.nanoTime() - start;
		
		System.out.println("BigSelect elapsed " + elapsed + " " + c + " " + (elapsed / c));
		
	}
	
	private void runSmallSelect() throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM test_db_object LIMIT 1");
		
		long start = System.nanoTime();

		ValidateHandler handler = new ValidateHandler();
		for(int i = 0; i < 1000000; i++) {
			ResultSet rs = ps.executeQuery();
			mapper.forEach(rs, handler );
		
		}
		long elapsed = System.nanoTime() - start;
		
		System.out.println("SmallSelect elapsed " + elapsed + " " + handler.c + " " + (elapsed / handler.c));
		
	}
	
	public static void main(String[] args) throws Exception {
		SimpleFlatMapperBenchmark benchmark = new SimpleFlatMapperBenchmark();
		for(int i = 0; i < 20; i++) {
			benchmark.runBigSelect();
		}
		
		for(int i = 0; i < 20; i++) {
			benchmark.runSmallSelect();
		}
	}


}
