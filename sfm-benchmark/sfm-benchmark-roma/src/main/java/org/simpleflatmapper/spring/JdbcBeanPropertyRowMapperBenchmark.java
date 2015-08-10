package org.simpleflatmapper.spring;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.simpleflatmapper.beans.MappedObject4;
import org.simpleflatmapper.beans.MappedObject16;
import org.simpleflatmapper.db.ConnectionParam;
import org.simpleflatmapper.db.ResultSetHandler;
import org.simpleflatmapper.param.LimitParam;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@State(Scope.Benchmark)
public class JdbcBeanPropertyRowMapperBenchmark {
	
	private BeanPropertyRowMapper<MappedObject4> mapper4;
	private BeanPropertyRowMapper<MappedObject16> mapper16;

	@Setup
	public void init() {
		mapper4 = new BeanPropertyRowMapper<MappedObject4>(MappedObject4.class);
		mapper16 = new BeanPropertyRowMapper<MappedObject16>(MappedObject16.class);
	}
	
	@Benchmark
	public void _04Fields(ConnectionParam connectionHolder, LimitParam limit, final Blackhole blackhole) throws Exception {
		connectionHolder.executeStatement(MappedObject4.SELECT_WITH_LIMIT,
				new ResultSetHandler() {
					@Override
					public void handle(ResultSet rs) throws Exception {
						int i = 0;
						while (rs.next()) {
							MappedObject4 o = mapper4.mapRow(rs, i);
							blackhole.consume(o);
							i++;
						}

					}
				}, limit.limit);
	}

	@Benchmark
	public void _16Fields(ConnectionParam connectionHolder, LimitParam limit, final Blackhole blackhole) throws Exception {
		connectionHolder.executeStatement(MappedObject16.SELECT_WITH_LIMIT,
				new ResultSetHandler() {
					@Override
					public void handle(ResultSet rs) throws Exception {
						int i = 0;
						while (rs.next()) {
							MappedObject16 o = mapper16.mapRow(rs, i);
							blackhole.consume(o);
							i++;
						}

					}
				}, limit.limit);
	}
}
