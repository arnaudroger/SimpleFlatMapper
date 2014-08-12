package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


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
}
