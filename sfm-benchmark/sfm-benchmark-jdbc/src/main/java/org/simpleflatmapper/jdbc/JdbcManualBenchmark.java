package org.simpleflatmapper.jdbc;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.simpleflatmapper.beans.MappedObject16;
import org.simpleflatmapper.beans.MappedObject4;
import org.simpleflatmapper.db.ConnectionParam;
import org.simpleflatmapper.db.ResultSetHandler;
import org.simpleflatmapper.db.RowMapper;
import org.simpleflatmapper.param.LimitParam;

import java.sql.ResultSet;

@State(Scope.Benchmark)
public class JdbcManualBenchmark {
	private RowMapper<MappedObject4> mapper4;
	private RowMapper<MappedObject16> mapper16;

	@Setup
	public void init() {
		mapper4 = new RowMapper<MappedObject4>() {
			@Override
			public MappedObject4 map(ResultSet rs) throws Exception {
				MappedObject4 o = new MappedObject4();
				o.setId(rs.getLong(1));
				o.setName(rs.getString(2));
				o.setEmail(rs.getString(3));
				o.setYearStarted(rs.getInt(4));
				return o;
			}
		};
		mapper16 = new RowMapper<MappedObject16>() {
			@Override
			public MappedObject16 map(ResultSet rs) throws Exception {
				MappedObject16 o = new MappedObject16();
				o.setId(rs.getLong(1));
				o.setName(rs.getString(2));
				o.setEmail(rs.getString(3));
				o.setYearStarted(rs.getInt(4));

				o.setField5(rs.getShort(5));
				o.setField6(rs.getInt(6));
				o.setField7(rs.getLong(7));
				o.setField8(rs.getFloat(8));
				o.setField9(rs.getDouble(9));

				o.setField10(rs.getShort(10));
				o.setField11(rs.getInt(11));
				o.setField12(rs.getLong(12));
				o.setField13(rs.getFloat(13));
				o.setField14(rs.getDouble(14));
				o.setField14(rs.getDouble(14));

				o.setField15(rs.getInt(15));
				o.setField16(rs.getInt(16));
				return o;
			}
		};
	}
	
	@SuppressWarnings("JpaQueryApiInspection")
	@Benchmark
	public void _04Fields(ConnectionParam connectionHolder, LimitParam limit, final Blackhole blackhole) throws Exception {
		connectionHolder.executeStatement(
				MappedObject4.SELECT_WITH_LIMIT,
				new ResultSetHandler() {
					@Override
					public void handle(ResultSet rs) throws Exception {
						while (rs.next()) {
							blackhole.consume(mapper4.map(rs));
						}
					}
				},
				limit.limit
		);
	}

	@SuppressWarnings("JpaQueryApiInspection")
	@Benchmark
	public void _16Fields(ConnectionParam connectionHolder, LimitParam limit, final Blackhole blackhole) throws Exception {
		connectionHolder.executeStatement(
				MappedObject16.SELECT_WITH_LIMIT,
				new ResultSetHandler() {
					@Override
					public void handle(ResultSet rs) throws Exception {
						while (rs.next()) {
							blackhole.consume(mapper16.map(rs));
						}
					}
				},
				limit.limit
		);
	}

}
