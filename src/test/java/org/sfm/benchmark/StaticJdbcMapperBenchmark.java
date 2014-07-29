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
init db
init db done
BigSelect elapsed 2872878000 1000000 2872
BigSelect elapsed 547079000 1000000 547
BigSelect elapsed 517898000 1000000 517
BigSelect elapsed 519572000 1000000 519
BigSelect elapsed 466686000 1000000 466
BigSelect elapsed 466480000 1000000 466
BigSelect elapsed 468322000 1000000 468
BigSelect elapsed 458678000 1000000 458
BigSelect elapsed 461590000 1000000 461
BigSelect elapsed 472170000 1000000 472
BigSelect elapsed 459041000 1000000 459
BigSelect elapsed 526015000 1000000 526
BigSelect elapsed 510849000 1000000 510
BigSelect elapsed 511078000 1000000 511
BigSelect elapsed 455981000 1000000 455
BigSelect elapsed 462642000 1000000 462
BigSelect elapsed 471440000 1000000 471
BigSelect elapsed 476329000 1000000 476
BigSelect elapsed 494994000 1000000 494
BigSelect elapsed 486891000 1000000 486
SmallSelect elapsed 1588171000 1000000 1588
SmallSelect elapsed 1420754000 1000000 1420
SmallSelect elapsed 1362580000 1000000 1362
SmallSelect elapsed 1372336000 1000000 1372
SmallSelect elapsed 1368552000 1000000 1368
SmallSelect elapsed 1371845000 1000000 1371
SmallSelect elapsed 1380888000 1000000 1380
SmallSelect elapsed 1360974000 1000000 1360
SmallSelect elapsed 1601812000 1000000 1601
SmallSelect elapsed 1392416000 1000000 1392
SmallSelect elapsed 1371163000 1000000 1371
SmallSelect elapsed 1353447000 1000000 1353
SmallSelect elapsed 1347083000 1000000 1347
SmallSelect elapsed 1372014000 1000000 1372
SmallSelect elapsed 1385395000 1000000 1385
SmallSelect elapsed 1346350000 1000000 1346
SmallSelect elapsed 1360180000 1000000 1360
SmallSelect elapsed 1359983000 1000000 1359
SmallSelect elapsed 1345334000 1000000 1345
SmallSelect elapsed 1359083000 1000000 1359


 */
public class StaticJdbcMapperBenchmark {
	JdbcMapper<DbObject> mapper;
	public StaticJdbcMapperBenchmark() throws NoSuchMethodException, SecurityException, SQLException {
		
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
		StaticJdbcMapperBenchmark benchmark = new StaticJdbcMapperBenchmark();
		for(int i = 0; i < 20; i++) {
			benchmark.runBigSelect();
		}
		
		for(int i = 0; i < 20; i++) {
			benchmark.runSmallSelect();
		}
	}


}
