package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.jdbc.DbHelper;


public class PureJdbcBenchmark implements QueryExecutor {

	final Connection conn;
	final Class<?> target;
	public PureJdbcBenchmark(Connection conn, Class<?> target) {
		this.conn = conn;
		this.target = target;
	}
	
	@Override
	public final void forEach(final ForEachListener ql, int limit) throws Exception {
		
	
		PreparedStatement ps = conn.prepareStatement(JDBCHelper.query(target, limit));
		RowMapper<?> mapper = JDBCHelper.mapper(target);
		try {
			ResultSet rs = ps.executeQuery();
			try {
				while(rs.next()) {
					ql.object(mapper.map(rs));
				}
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}
	
	public static void main(String[] args) throws SQLException, Exception {
		AllBenchmark.runBenchmark(DbHelper.benchmarkDb(), SmallBenchmarkObject.class, PureJdbcBenchmark.class, 1000, 100000);
	}
}
