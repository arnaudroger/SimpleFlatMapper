package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;


public class PureJdbcBenchmark implements QueryExecutor {

	final  Connection conn;
	
	public PureJdbcBenchmark(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public final void forEach(final ForEachListener ql, int limit) throws Exception {
		
		StringBuilder query = buildQuery(limit);
	
		PreparedStatement ps = conn.prepareStatement(query.toString());
		
		try {
			ResultSet rs = ps.executeQuery();
			try {
				while(rs.next()) {
					ql.object(newDbObject(rs));
				}
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}

	private StringBuilder buildQuery(int limit) {
		StringBuilder query = new StringBuilder("SELECT id, name, email, creation_time FROM test_db_object ");
		if (limit >= 0) {
			query.append("LIMIT ").append(Integer.toString(limit));
		}
		return query;
	}

	private DbObject newDbObject(ResultSet rs) throws SQLException {
		DbObject o = new DbObject();
		o.setId(rs.getLong(1));
		o.setName(rs.getString(2));
		o.setEmail(rs.getString(3));
		o.setCreationTime(rs.getTimestamp(4));
		return o;
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException, Exception {
		new BenchmarkRunner(-1, new PureJdbcBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(PureJdbcBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new PureJdbcBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(PureJdbcBenchmark.class, "SmallQuery"));
	}
}
