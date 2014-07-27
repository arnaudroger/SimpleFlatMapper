package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;

public class ReferenceBigSelectBenchmark {

	public ReferenceBigSelectBenchmark() {
	}
	
	private void run() throws Exception {
		Connection conn = DbHelper.benchmarkDb();
		
		PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, creation_time FROM test_db_object");

		ResultSet rs = ps.executeQuery();
		
		long start = System.nanoTime();
		
		long c = 0;
		
		while(rs.next()) {
			DbObject o = new DbObject();
			o.setId(rs.getLong(1));
			o.setName(rs.getString(2));
			o.setEmail(rs.getString(3));
			o.setCreationTime(rs.getTimestamp(4));
			c++;
		}
		
		long elapsed = System.nanoTime() - start;
		
		System.out.println("elapased " + elapsed + " " + c + " " + (elapsed / c));
		
	}
	
	public static void main(String[] args) throws Exception {
		ReferenceBigSelectBenchmark benchmark = new ReferenceBigSelectBenchmark();
		for(int i = 0; i < 20; i++) {
			benchmark.run();
		}
	}


}
