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

/*
Benchmark                                  (db)  (limit)   Mode  Cnt        Score        Error  Units
o.s.jdbc.JdbcManualBenchmark._04Fields       H2        1  thrpt   20  2756228.712 ± 154235.112  ops/s
o.s.jdbc.JdbcManualBenchmark._04Fields       H2       10  thrpt   20  1041612.403 ±  68936.146  ops/s
o.s.jdbc.JdbcManualBenchmark._04Fields       H2      100  thrpt   20   153813.834 ±  10129.339  ops/s
o.s.jdbc.JdbcManualBenchmark._04Fields       H2     1000  thrpt   20    15753.537 ±    545.082  ops/s
o.s.jdbc.JdbcManualBenchmark._16Fields       H2        1  thrpt   20  1381933.078 ±  99518.858  ops/s
o.s.jdbc.JdbcManualBenchmark._16Fields       H2       10  thrpt   20   319305.664 ±  26296.415  ops/s
o.s.jdbc.JdbcManualBenchmark._16Fields       H2      100  thrpt   20    38335.997 ±   3255.070  ops/s
o.s.jdbc.JdbcManualBenchmark._16Fields       H2     1000  thrpt   20     3998.312 ±    179.955  ops/s
o.s.sfm.JdbcSfmDynamicBenchmark._04Fields    H2        1  thrpt   20  1685787.795 ± 105675.406  ops/s
o.s.sfm.JdbcSfmDynamicBenchmark._04Fields    H2       10  thrpt   20   704580.729 ±  37947.689  ops/s
o.s.sfm.JdbcSfmDynamicBenchmark._04Fields    H2      100  thrpt   20   110332.643 ±   6753.233  ops/s
o.s.sfm.JdbcSfmDynamicBenchmark._04Fields    H2     1000  thrpt   20    12204.286 ±    898.711  ops/s
o.s.sfm.JdbcSfmDynamicBenchmark._16Fields    H2        1  thrpt   20   660804.909 ±  50039.235  ops/s
o.s.sfm.JdbcSfmDynamicBenchmark._16Fields    H2       10  thrpt   20   208976.163 ±  12108.967  ops/s
o.s.sfm.JdbcSfmDynamicBenchmark._16Fields    H2      100  thrpt   20    32331.790 ±    383.428  ops/s
o.s.sfm.JdbcSfmDynamicBenchmark._16Fields    H2     1000  thrpt   20     3274.331 ±    127.009  ops/s
o.s.sfm.JdbcSfmStaticBenchmark._04Fields     H2        1  thrpt   20  2418654.404 ± 190531.372  ops/s
o.s.sfm.JdbcSfmStaticBenchmark._04Fields     H2       10  thrpt   20   826200.614 ±  79170.704  ops/s
o.s.sfm.JdbcSfmStaticBenchmark._04Fields     H2      100  thrpt   20   117513.263 ±   9081.209  ops/s
o.s.sfm.JdbcSfmStaticBenchmark._04Fields     H2     1000  thrpt   20    11820.698 ±    891.242  ops/s
o.s.sfm.JdbcSfmStaticBenchmark._16Fields     H2        1  thrpt   20  1244043.513 ±  77917.986  ops/s
o.s.sfm.JdbcSfmStaticBenchmark._16Fields     H2       10  thrpt   20   270888.863 ±  19701.639  ops/s
o.s.sfm.JdbcSfmStaticBenchmark._16Fields     H2      100  thrpt   20    29801.096 ±   1437.297  ops/s
o.s.sfm.JdbcSfmStaticBenchmark._16Fields     H2     1000  thrpt   20     3115.672 ±    222.970  ops/s

 */
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
