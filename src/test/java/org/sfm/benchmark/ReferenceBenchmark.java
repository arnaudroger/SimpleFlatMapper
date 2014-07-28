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
init db
init db done
BigSelect elapsed  2733624000 1000000 2733
BigSelect elapsed  478547000 1000000 478
BigSelect elapsed  416367000 1000000 416
BigSelect elapsed  383644000 1000000 383
BigSelect elapsed  385196000 1000000 385
BigSelect elapsed  380017000 1000000 380
BigSelect elapsed  421670000 1000000 421
BigSelect elapsed  387768000 1000000 387
BigSelect elapsed  373889000 1000000 373
BigSelect elapsed  429772000 1000000 429
BigSelect elapsed  373167000 1000000 373
BigSelect elapsed  377310000 1000000 377
BigSelect elapsed  424532000 1000000 424
BigSelect elapsed  403497000 1000000 403
BigSelect elapsed  380374000 1000000 380
BigSelect elapsed  425855000 1000000 425
BigSelect elapsed  395031000 1000000 395
BigSelect elapsed  381931000 1000000 381
BigSelect elapsed  373564000 1000000 373
BigSelect elapsed  384799000 1000000 384
SmallSelect elapsed 1641326000 1000000 1641
SmallSelect elapsed 1452267000 1000000 1452
SmallSelect elapsed 1461761000 1000000 1461
SmallSelect elapsed 1470915000 1000000 1470
SmallSelect elapsed 1487996000 1000000 1487
SmallSelect elapsed 1473714000 1000000 1473
SmallSelect elapsed 1471531000 1000000 1471
SmallSelect elapsed 1471904000 1000000 1471
SmallSelect elapsed 1441702000 1000000 1441
SmallSelect elapsed 1465890000 1000000 1465
SmallSelect elapsed 1463843000 1000000 1463
SmallSelect elapsed 1466411000 1000000 1466
SmallSelect elapsed 1480461000 1000000 1480
SmallSelect elapsed 1485419000 1000000 1485
SmallSelect elapsed 1460172000 1000000 1460
SmallSelect elapsed 1488493000 1000000 1488
SmallSelect elapsed 1483008000 1000000 1483
SmallSelect elapsed 1464952000 1000000 1464
SmallSelect elapsed 1479110000 1000000 1479
SmallSelect elapsed 1465201000 1000000 1465


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
