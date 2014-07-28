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
BigSelect elapsed 3086061000 1000000 3086
BigSelect elapsed 692520000 1000000 692
BigSelect elapsed 598618000 1000000 598
BigSelect elapsed 612968000 1000000 612
BigSelect elapsed 602932000 1000000 602
BigSelect elapsed 591258000 1000000 591
BigSelect elapsed 595497000 1000000 595
BigSelect elapsed 597289000 1000000 597
BigSelect elapsed 603572000 1000000 603
BigSelect elapsed 600956000 1000000 600
BigSelect elapsed 547474000 1000000 547
BigSelect elapsed 559442000 1000000 559
BigSelect elapsed 570754000 1000000 570
BigSelect elapsed 583284000 1000000 583
BigSelect elapsed 572225000 1000000 572
BigSelect elapsed 576149000 1000000 576
BigSelect elapsed 576495000 1000000 576
BigSelect elapsed 575661000 1000000 575
BigSelect elapsed 575715000 1000000 575
BigSelect elapsed 575256000 1000000 575
SmallSelect elapsed 1855721000 1000000 1855
SmallSelect elapsed 1614995000 1000000 1614
SmallSelect elapsed 1644199000 1000000 1644
SmallSelect elapsed 1650592000 1000000 1650
SmallSelect elapsed 1640954000 1000000 1640
SmallSelect elapsed 1648930000 1000000 1648
SmallSelect elapsed 1651306000 1000000 1651
SmallSelect elapsed 1659022000 1000000 1659
SmallSelect elapsed 1639301000 1000000 1639
SmallSelect elapsed 1685093000 1000000 1685
SmallSelect elapsed 1638141000 1000000 1638
SmallSelect elapsed 1640541000 1000000 1640
SmallSelect elapsed 1625359000 1000000 1625
SmallSelect elapsed 1615299000 1000000 1615
SmallSelect elapsed 1633742000 1000000 1633
SmallSelect elapsed 1639503000 1000000 1639
SmallSelect elapsed 1619269000 1000000 1619
SmallSelect elapsed 1646437000 1000000 1646
SmallSelect elapsed 1614347000 1000000 1614
SmallSelect elapsed 1640975000 1000000 1640
 */
public class DynamicJdbcMapperMapBenchmark {
	JdbcMapper<DbObject> mapper;
	public DynamicJdbcMapperMapBenchmark() throws NoSuchMethodException, SecurityException, SQLException {
		mapper = ResultSetMapperFactory.newMapper(DbObject.class);
	}
	
	private void runBigSelect() throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM test_db_object");
		
		ResultSet rs = ps.executeQuery();
		
		long start = System.nanoTime();
		
		ValidateHandler handler = new ValidateHandler();
		
		forEach(rs, handler);
		
		long elapsed = System.nanoTime() - start;
		
		System.out.println("BigSelect elapsed " + elapsed + " " + handler.c + " " + (elapsed / handler.c));
		
	}

	private void forEach(ResultSet rs, ValidateHandler handler)
			throws SQLException, Exception {
		while(rs.next()) {
			DbObject o = new DbObject();
			mapper.map(rs, o);
			handler.handle(o);
			
		}
	}
	
	private void runSmallSelect() throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM test_db_object LIMIT 1");
		
		long start = System.nanoTime();

		ValidateHandler handler = new ValidateHandler();
		for(int i = 0; i < 1000000; i++) {
			ResultSet rs = ps.executeQuery();
			forEach(rs, handler);
		}
		long elapsed = System.nanoTime() - start;
		
		System.out.println("SmallSelect elapsed " + elapsed + " " + handler.c + " " + (elapsed / handler.c));
		
	}
	
	public static void main(String[] args) throws Exception {
		DynamicJdbcMapperMapBenchmark benchmark = new DynamicJdbcMapperMapBenchmark();
		for(int i = 0; i < 20; i++) {
			benchmark.runBigSelect();
		}
		
		for(int i = 0; i < 20; i++) {
			benchmark.runSmallSelect();
		}
	}


}
