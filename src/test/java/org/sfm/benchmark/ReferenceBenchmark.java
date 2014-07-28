package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;


/*
init db
init db done
BigSelect elapsed  4045788000 1000000 4045
BigSelect elapsed  629981000 1000000 629
BigSelect elapsed  491926000 1000000 491
BigSelect elapsed  493075000 1000000 493
BigSelect elapsed  491405000 1000000 491
BigSelect elapsed  496468000 1000000 496
BigSelect elapsed  553518000 1000000 553
BigSelect elapsed  468903000 1000000 468
BigSelect elapsed  478725000 1000000 478
BigSelect elapsed  573720000 1000000 573
BigSelect elapsed  466093000 1000000 466
BigSelect elapsed  488112000 1000000 488
BigSelect elapsed  597355000 1000000 597
BigSelect elapsed  482435000 1000000 482
BigSelect elapsed  468965000 1000000 468
BigSelect elapsed  560380000 1000000 560
BigSelect elapsed  500890000 1000000 500
BigSelect elapsed  498847000 1000000 498
BigSelect elapsed  480548000 1000000 480
BigSelect elapsed  536597000 1000000 536
SmallSelect elapsed 2004039000 1000000 2004
SmallSelect elapsed 1543379000 1000000 1543
SmallSelect elapsed 1515027000 1000000 1515
SmallSelect elapsed 1557402000 1000000 1557
SmallSelect elapsed 1602739000 1000000 1602
SmallSelect elapsed 1587954000 1000000 1587
SmallSelect elapsed 1512615000 1000000 1512
SmallSelect elapsed 1659874000 1000000 1659
SmallSelect elapsed 1855781000 1000000 1855
SmallSelect elapsed 1771819000 1000000 1771
SmallSelect elapsed 1945055000 1000000 1945
SmallSelect elapsed 1875370000 1000000 1875
SmallSelect elapsed 1815678000 1000000 1815
SmallSelect elapsed 1766755000 1000000 1766
SmallSelect elapsed 1872997000 1000000 1872
SmallSelect elapsed 1764635000 1000000 1764
SmallSelect elapsed 1887611000 1000000 1887
SmallSelect elapsed 1865662000 1000000 1865
SmallSelect elapsed 1794183000 1000000 1794
SmallSelect elapsed 1844881000 1000000 1844

 */
public class ReferenceBenchmark {

	public ReferenceBenchmark() {
	}
	
	private void runBigSelect() throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		
		PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, creation_time FROM test_db_object");

		ResultSet rs = ps.executeQuery();
		
		long start = System.nanoTime();
		
		ValidateHandler handler = new ValidateHandler();
		
		forEach(handler, rs);
		
		long elapsed = System.nanoTime() - start;
		
		System.out.println("BigSelect elapsed  " + elapsed + " " +  handler.c + " " + (elapsed /  handler.c));
		
	}
	
	private void runSmallSelect() throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM test_db_object LIMIT 1");
		
		long start = System.nanoTime();

		ValidateHandler handler = new ValidateHandler();
		for(int i = 0; i < 1000000; i++) {
			ResultSet rs = ps.executeQuery();
			forEach(handler, rs);
		}
		long elapsed = System.nanoTime() - start;
		
		System.out.println("SmallSelect elapsed " + elapsed + " " + handler.c + " " + (elapsed / handler.c));
		
	}

	private void forEach(ValidateHandler handler, ResultSet rs)
			throws SQLException, Exception {
		while(rs.next()){
			DbObject o = newDbObject(rs);
			handler.handle(o);
		}
	}

	private DbObject newDbObject(ResultSet rs) throws SQLException {
		DbObject o = new DbObject();
		o.setId(rs.getLong(1));
		o.setName(rs.getString(2));
		o.setEmail(rs.getString(3));
		o.setCreationTime(rs.getTimestamp(4));
		return o;
	}
	
	public static void main(String[] args) throws Exception {
		ReferenceBenchmark benchmark = new ReferenceBenchmark();
		for(int i = 0; i < 20; i++) {
			benchmark.runBigSelect();
		}
		for(int i = 0; i < 20; i++) {
			benchmark.runSmallSelect();
		}
	}


}
