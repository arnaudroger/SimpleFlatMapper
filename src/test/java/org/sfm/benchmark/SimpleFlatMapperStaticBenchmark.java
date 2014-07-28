package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.DelegateJdbcMapper;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.ResultSetMapperBuilder;
import org.sfm.reflect.Instantiator;

/*
init db
init db done
BigSelect elapsed 3886307000 1000000 3886
BigSelect elapsed 735384000 1000000 735
BigSelect elapsed 684350000 1000000 684
BigSelect elapsed 689482000 1000000 689
BigSelect elapsed 701372000 1000000 701
BigSelect elapsed 623838000 1000000 623
BigSelect elapsed 608107000 1000000 608
BigSelect elapsed 576389000 1000000 576
BigSelect elapsed 626839000 1000000 626
BigSelect elapsed 687534000 1000000 687
BigSelect elapsed 708666000 1000000 708
BigSelect elapsed 688244000 1000000 688
BigSelect elapsed 679142000 1000000 679
BigSelect elapsed 657913000 1000000 657
BigSelect elapsed 656848000 1000000 656
BigSelect elapsed 685421000 1000000 685
BigSelect elapsed 661396000 1000000 661
BigSelect elapsed 633898000 1000000 633
BigSelect elapsed 593492000 1000000 593
BigSelect elapsed 609580000 1000000 609
SmallSelect elapsed 2079903000 1000000 2079
SmallSelect elapsed 1853800000 1000000 1853
SmallSelect elapsed 1792647000 1000000 1792
SmallSelect elapsed 1846062000 1000000 1846
SmallSelect elapsed 1797794000 1000000 1797
SmallSelect elapsed 1776067000 1000000 1776
SmallSelect elapsed 1811283000 1000000 1811
SmallSelect elapsed 1780285000 1000000 1780
SmallSelect elapsed 1769303000 1000000 1769
SmallSelect elapsed 2110649000 1000000 2110
SmallSelect elapsed 1946382000 1000000 1946
SmallSelect elapsed 2110848000 1000000 2110
SmallSelect elapsed 2013508000 1000000 2013
SmallSelect elapsed 2104571000 1000000 2104
SmallSelect elapsed 1962064000 1000000 1962
SmallSelect elapsed 2036549000 1000000 2036
SmallSelect elapsed 1957172000 1000000 1957
SmallSelect elapsed 2100799000 1000000 2100
SmallSelect elapsed 1978547000 1000000 1978
SmallSelect elapsed 2122127000 1000000 2122

 */
public class SimpleFlatMapperStaticBenchmark {
	JdbcMapper<DbObject> mapper;
	public SimpleFlatMapperStaticBenchmark() throws NoSuchMethodException, SecurityException, SQLException {
		
		mapper = new DelegateJdbcMapper<DbObject>(new ResultSetMapperBuilder<DbObject>(DbObject.class).addIndexedColumn("id").addIndexedColumn("name").addIndexedColumn("email").addIndexedColumn("creation_time").mapper(), 
				new Instantiator<DbObject>() {
					@Override
					public DbObject newInstance() throws Exception {
						return new DbObject();
					}
				});
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
		SimpleFlatMapperStaticBenchmark benchmark = new SimpleFlatMapperStaticBenchmark();
		for(int i = 0; i < 20; i++) {
			benchmark.runBigSelect();
		}
		
		for(int i = 0; i < 20; i++) {
			benchmark.runSmallSelect();
		}
	}


}
