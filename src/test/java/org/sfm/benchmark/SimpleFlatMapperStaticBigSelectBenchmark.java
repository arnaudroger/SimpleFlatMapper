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
elapased 709065000 1000000 709
elapased 622364000 1000000 622
elapased 556831000 1000000 556
elapased 563116000 1000000 563
elapased 547182000 1000000 547
elapased 480050000 1000000 480
elapased 476315000 1000000 476
elapased 489417000 1000000 489
elapased 563452000 1000000 563
elapased 542751000 1000000 542
elapased 564681000 1000000 564
elapased 496321000 1000000 496
elapased 493212000 1000000 493
elapased 566663000 1000000 566
elapased 487017000 1000000 487
elapased 512742000 1000000 512
elapased 567498000 1000000 567
elapased 479433000 1000000 479
elapased 478848000 1000000 478
elapased 512536000 1000000 512
 */
public class SimpleFlatMapperStaticBigSelectBenchmark {
	JdbcMapper<DbObject> mapper;
	public SimpleFlatMapperStaticBigSelectBenchmark() throws NoSuchMethodException, SecurityException, SQLException {
		
		mapper = new DelegateJdbcMapper<DbObject>(new ResultSetMapperBuilder<DbObject>(DbObject.class).addIndexedColumn("id").addIndexedColumn("name").addIndexedColumn("email").addIndexedColumn("creation_time").mapper(), 
				new Instantiator<DbObject>() {
					@Override
					public DbObject newInstance() throws Exception {
						return new DbObject();
					}
				});
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
		SimpleFlatMapperStaticBigSelectBenchmark benchmark = new SimpleFlatMapperStaticBigSelectBenchmark();
		for(int i = 0; i < 20; i++) {
			benchmark.run();
		}
	}


}
