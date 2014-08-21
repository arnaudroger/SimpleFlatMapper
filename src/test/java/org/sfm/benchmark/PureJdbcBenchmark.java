package org.sfm.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.jdbc.AsmHelper;
import org.sfm.jdbc.DbHelper;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;


public class PureJdbcBenchmark<T> implements QueryExecutor {

	final Connection conn;
	final Class<T> target;
	final RowMapper<T> mapper; 
	final Instantiator<T> instantiator;
	
	public PureJdbcBenchmark(Connection conn, Class<T> target) throws NoSuchMethodException, SecurityException {
		this.conn = conn;
		this.target = target;
		this.mapper = JDBCHelper.mapper(target);
		this.instantiator = new InstantiatorFactory(AsmHelper.getAsmSetterFactory()).getInstantiator(target);
	}
	
	@Override
	public final void forEach(final ForEachListener ql, int limit) throws Exception {
		PreparedStatement ps = conn.prepareStatement(JDBCHelper.query(target, limit));
		try {
			ResultSet rs = ps.executeQuery();
			
			try {
				forEach(rs, ql);
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}

	private void forEach(ResultSet rs, final ForEachListener ql) throws SQLException, Exception {
		while(rs.next()) {
			T o = mapper.map(rs);
			ql.object(o);
		}
	}
	
	public static void main(String[] args) throws SQLException, Exception {
		AllBenchmark.runBenchmark(DbHelper.benchmarkDb(), SmallBenchmarkObject.class, PureJdbcBenchmark.class, BenchmarkConstants.SINGLE_QUERY_SIZE, BenchmarkConstants.SINGLE_NB_ITERATION);
	}
}
