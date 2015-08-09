package org.simpleflatmapper.sfm;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.reflect.asm.AsmHelper;
import org.sfm.utils.RowHandler;
import org.simpleflatmapper.beans.MappedObject16;
import org.simpleflatmapper.beans.MappedObject4;
import org.simpleflatmapper.db.ConnectionParam;
import org.simpleflatmapper.db.ResultSetHandler;
import org.simpleflatmapper.jdbc.JdbcManualBenchmark;
import org.simpleflatmapper.param.LimitParam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@State(Scope.Benchmark)
public class JdbcSfmDynamicBenchmark {

	private JdbcMapper<MappedObject4> mapper4;
	private JdbcMapper<MappedObject16> mapper16;
	@Setup
	public void init() {
		if (! AsmHelper.isAsmPresent()) {
			throw new RuntimeException("Asm not present or incompatible");
		}
		
		mapper4 = JdbcMapperFactory.newInstance().newMapper(MappedObject4.class);
		mapper16 = JdbcMapperFactory.newInstance().newMapper(MappedObject16.class);
	}

	@Benchmark
	public void _04Fields(ConnectionParam connectionParam, LimitParam limitParam, final Blackhole blackhole) throws Exception {
		connectionParam.executeStatement(JdbcManualBenchmark.SELECT_BENCHMARK_OBJ_WITH_LIMIT,
				new ResultSetHandler() {
					@Override
					public void handle(ResultSet rs) throws Exception {
						mapper4.forEach(rs, new RowHandler<MappedObject4>() {
							@Override
							public void handle(MappedObject4 mappedObject4) throws Exception {
								blackhole.consume(mappedObject4);
							}
						});
					}
				}, limitParam.limit);
	}
	@Benchmark
	public void _16Fields(ConnectionParam connectionParam, LimitParam limitParam, final Blackhole blackhole) throws Exception {
		connectionParam.executeStatement(JdbcManualBenchmark.SELECT_BIG_OBJ_WITH_LIMIT,
				new ResultSetHandler() {
					@Override
					public void handle(ResultSet rs) throws Exception {
						mapper16.forEach(rs, new RowHandler<MappedObject16>() {
							@Override
							public void handle(MappedObject16 mappedObject4) throws Exception {
								blackhole.consume(mappedObject4);
							}
						});
					}
				}, limitParam.limit);
	}
}
