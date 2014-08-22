package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.JDBCHelper;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.utils.Handler;

public class ForEachMapperQueryExecutor<T> implements QueryExecutor {

	final JdbcMapper<T> mapper;
	final  Connection conn;
	final Class<T> target;
	
	
	public ForEachMapperQueryExecutor(JdbcMapper<T> mapper,
			Connection conn, Class<T> target) {
		super();
		this.mapper = mapper;
		this.conn = conn;
		this.target = target;
	}
	
	public final void forEach(final ForEachListener ql, int limit) throws Exception {
		PreparedStatement ps = conn.prepareStatement(JDBCHelper.query(target, limit));
		
		try {
			ResultSet rs = ps.executeQuery();
			try {
				mapper.forEach(rs, new Handler<T>() {
					@Override
					public void handle(T t) throws Exception {
						ql.object(t);
					}
				});
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}
	
}