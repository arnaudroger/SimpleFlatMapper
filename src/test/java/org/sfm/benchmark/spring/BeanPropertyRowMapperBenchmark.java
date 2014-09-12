package org.sfm.benchmark.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.JDBCHelper;
import org.sfm.benchmark.QueryExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class BeanPropertyRowMapperBenchmark<T> implements QueryExecutor {
	final  Connection conn;
	final Class<T> target;
	final BeanPropertyRowMapper<T> rowMapper;
	
	
	public BeanPropertyRowMapperBenchmark(Connection conn, Class<T> target) {
		super();
		this.conn = conn;
		this.target = target;
		this.rowMapper = new BeanPropertyRowMapper<T>(target);
	}


	@Override
	public void forEach(ForEachListener ql, int limit) throws Exception {
		PreparedStatement ps = conn.prepareStatement(JDBCHelper.query(target, limit));
		
		try {
			ResultSet rs = ps.executeQuery();
			try {
				int i = 0;
				while(rs.next()) {
					ql.object(rowMapper.mapRow(rs, i));
					i++;
				}
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}

}
