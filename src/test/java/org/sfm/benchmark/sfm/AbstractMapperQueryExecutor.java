package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sfm.beans.DbObject;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.jdbc.JdbcMapper;

public abstract class AbstractMapperQueryExecutor implements QueryExecutor {

	final JdbcMapper<DbObject> mapper;
	final  Connection conn;
	
	
	
	public AbstractMapperQueryExecutor(JdbcMapper<DbObject> mapper,
			Connection conn) {
		super();
		this.mapper = mapper;
		this.conn = conn;
	}

	
	public final void forEach(final ForEachListener ql, int limit) throws Exception {
		StringBuilder query = new StringBuilder("SELECT id, name, email, creation_time FROM test_db_object ");
		if (limit >= 0) {
			query.append("LIMIT ").append(Integer.toString(limit));
		}
	
		PreparedStatement ps = conn.prepareStatement(query.toString());
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

	protected abstract void forEach(ResultSet rs, ForEachListener ql) throws Exception;
	
}