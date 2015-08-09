package org.simpleflatmapper.spring;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.simpleflatmapper.db.ConnectionParam;
import org.simpleflatmapper.param.LimitParam;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@State(Scope.Benchmark)
public class JdbcRomaBenchmark {
	private RowMapper<MappedObject4> mapper ;

	@Setup
	public void init() {
		this.mapper = RomaMapperFactory.getRowMapper();
	}

	@Benchmark
	public void testQuery(ConnectionParam connectionHolder, LimitParam limit, final Blackhole blackhole) throws Exception {
		Connection conn = connectionHolder.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id, name, email, year_started FROM test_small_benchmark_object LIMIT ?");
			try {
				ps.setInt(1, limit.limit);
				ResultSet rs = ps.executeQuery();
				int i = 0;
				while(rs.next()) {
					Object o = mapper.mapRow(rs, i);
					blackhole.consume(o);
					i++;
				}
			}finally {
				ps.close();
			}
		} finally {
			conn.close();
		}
	}
}
