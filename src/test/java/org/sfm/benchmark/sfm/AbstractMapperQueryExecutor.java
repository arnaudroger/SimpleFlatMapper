package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.jdbc.JdbcMapper;

public abstract class AbstractMapperQueryExecutor implements QueryExecutor {

	final JdbcMapper<DbObject> mapper;
	final  Connection conn;
	
	PreparedStatement ps;
	ResultSet rs;
	
	
	public AbstractMapperQueryExecutor(JdbcMapper<DbObject> mapper,
			Connection conn) {
		super();
		this.mapper = mapper;
		this.conn = conn;
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

}