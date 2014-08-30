package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.benchmark.QueryExecutor;
import org.sfm.benchmark.RunBenchmark;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.JdbcMapperFactory;

public class DynamicNoAsmJdbcMapperForEachBenchmark<T> extends ForEachMapperQueryExecutor<T> implements QueryExecutor {
	public DynamicNoAsmJdbcMapperForEachBenchmark(Connection conn, Class<T> target)
			throws NoSuchMethodException, SecurityException, SQLException {
		super(JdbcMapperFactory.newInstance().useAsm(false).newMapper(target), conn, target);
	}
	
	public static void main(String[] args) throws SQLException, Exception {
		RunBenchmark.runBenchmark(DbHelper.getConnection(args), DynamicNoAsmJdbcMapperForEachBenchmark.class);
	}
}
