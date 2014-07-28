package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;


/*

elapased 530475000 1000000 530
elapased 445398000 1000000 445
elapased 507337000 1000000 507
elapased 455670000 1000000 455
elapased 384460000 1000000 384
elapased 400342000 1000000 400
elapased 442569000 1000000 442
elapased 399914000 1000000 399
elapased 406357000 1000000 406
elapased 438062000 1000000 438
elapased 407320000 1000000 407
elapased 415381000 1000000 415
elapased 444106000 1000000 444
elapased 398666000 1000000 398
elapased 449121000 1000000 449
elapased 405771000 1000000 405
elapased 384581000 1000000 384
elapased 379616000 1000000 379
elapased 384504000 1000000 384
elapased 426004000 1000000 426
 */
public class ReferenceBigSelectBenchmark {

	public ReferenceBigSelectBenchmark() {
	}
	
	private void run() throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		
		PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, creation_time FROM test_db_object");

		ResultSet rs = ps.executeQuery();
		
		long start = System.nanoTime();
		
		ValidateHandler handler = new ValidateHandler();
		
		while(rs.next()) {
			DbObject o = new DbObject();
			o.setId(rs.getLong(1));
			o.setName(rs.getString(2));
			o.setEmail(rs.getString(3));
			o.setCreationTime(rs.getTimestamp(4));
			handler.handle(o);
		}
		
		long elapsed = System.nanoTime() - start;
		
		System.out.println("elapased " + elapsed + " " +  handler.c + " " + (elapsed /  handler.c));
		
	}
	
	public static void main(String[] args) throws Exception {
		ReferenceBigSelectBenchmark benchmark = new ReferenceBigSelectBenchmark();
		for(int i = 0; i < 20; i++) {
			benchmark.run();
		}
	}


}
