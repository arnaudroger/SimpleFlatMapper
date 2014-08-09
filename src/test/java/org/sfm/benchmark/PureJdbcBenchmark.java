package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;


public class PureJdbcBenchmark implements QueryExecutor {

	final  Connection conn;
	
	PreparedStatement ps;
	ResultSet rs;
	
	public PureJdbcBenchmark(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public final void forEach(final ForEachListener ql) throws Exception {
		while(rs.next()) {
			ql.object(newDbObject(rs));
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

	@Override
	public final void prepareQuery(int limit) throws Exception {
		StringBuilder query = new StringBuilder("SELECT id, name, email, creation_time FROM test_db_object ");
		if (limit >= 0) {
			query.append("LIMIT ").append(Integer.toString(limit));
		}
	
		ps = conn.prepareStatement(query.toString());
	}

	@Override
	public final void executeQuery() throws Exception {
		rs = ps.executeQuery();
	}

	@Override
	public final void close() throws SQLException {
		ps.close();
	}

	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException, Exception {
		new BenchmarkRunner(-1, new PureJdbcBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(PureJdbcBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new PureJdbcBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(PureJdbcBenchmark.class, "SmallQuery"));
	}


}
