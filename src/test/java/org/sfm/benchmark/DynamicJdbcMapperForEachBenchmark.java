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
init db
init db done
BigSelect elapsed 2954215000 1000000 2954
BigSelect elapsed 583761000 1000000 583
BigSelect elapsed 576504000 1000000 576
BigSelect elapsed 554269000 1000000 554
BigSelect elapsed 544940000 1000000 544
BigSelect elapsed 557194000 1000000 557
BigSelect elapsed 499717000 1000000 499
BigSelect elapsed 551560000 1000000 551
BigSelect elapsed 548357000 1000000 548
BigSelect elapsed 548670000 1000000 548
BigSelect elapsed 554564000 1000000 554
BigSelect elapsed 544458000 1000000 544
BigSelect elapsed 493516000 1000000 493
BigSelect elapsed 500239000 1000000 500
BigSelect elapsed 506217000 1000000 506
BigSelect elapsed 534747000 1000000 534
BigSelect elapsed 523316000 1000000 523
BigSelect elapsed 495783000 1000000 495
BigSelect elapsed 495529000 1000000 495
BigSelect elapsed 497480000 1000000 497
SmallSelect elapsed 1872859000 1000000 1872
SmallSelect elapsed 1623977000 1000000 1623
SmallSelect elapsed 1609756000 1000000 1609
SmallSelect elapsed 1620628000 1000000 1620
SmallSelect elapsed 1592841000 1000000 1592
SmallSelect elapsed 1613082000 1000000 1613
SmallSelect elapsed 1616709000 1000000 1616
SmallSelect elapsed 1612961000 1000000 1612
SmallSelect elapsed 1609605000 1000000 1609
SmallSelect elapsed 1603820000 1000000 1603
SmallSelect elapsed 1594391000 1000000 1594
SmallSelect elapsed 1589594000 1000000 1589
SmallSelect elapsed 1610686000 1000000 1610
SmallSelect elapsed 1553666000 1000000 1553
SmallSelect elapsed 1593065000 1000000 1593
SmallSelect elapsed 1593710000 1000000 1593
SmallSelect elapsed 1602672000 1000000 1602
SmallSelect elapsed 1621996000 1000000 1621
SmallSelect elapsed 1613452000 1000000 1613
SmallSelect elapsed 1536409000 1000000 1536

 */
public class DynamicJdbcMapperForEachBenchmark {
	JdbcMapper<DbObject> mapper;
	public DynamicJdbcMapperForEachBenchmark() throws NoSuchMethodException, SecurityException, SQLException {
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
		DynamicJdbcMapperForEachBenchmark benchmark = new DynamicJdbcMapperForEachBenchmark();
		for(int i = 0; i < 20; i++) {
			benchmark.runBigSelect();
		}
		
		for(int i = 0; i < 20; i++) {
			benchmark.runSmallSelect();
		}
	}


}
