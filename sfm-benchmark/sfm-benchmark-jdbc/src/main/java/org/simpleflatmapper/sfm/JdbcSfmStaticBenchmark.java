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
public class JdbcSfmStaticBenchmark {

	private JdbcMapper<MappedObject4> mapper4;
	private JdbcMapper<MappedObject16> mapper16;

	@Setup
	public void init() {
		if (! AsmHelper.isAsmPresent()) {
			throw new RuntimeException("Asm not present or incompatible");
		}

		mapper4 = JdbcMapperFactory.newInstance().newBuilder(MappedObject4.class)
				.addMapping("id")
				.addMapping("name")
				.addMapping("email")
				.addMapping("year_started").mapper();
		mapper16 = JdbcMapperFactory.newInstance().newBuilder(MappedObject16.class)
				.addMapping("id")
				.addMapping("name")
				.addMapping("email")
				.addMapping("year_started")
				.addMapping("field5")
				.addMapping("field6")
				.addMapping("field7")
				.addMapping("field8")
				.addMapping("field9")
				.addMapping("field10")
				.addMapping("field11")
				.addMapping("field12")
				.addMapping("field13")
				.addMapping("field14")
				.addMapping("field15")
				.addMapping("field16")
				.mapper();
	}

	@Benchmark
	public void _04Fields(ConnectionParam connectionParam, LimitParam limitParam, final Blackhole blackhole) throws Exception {
		connectionParam.executeStatement(MappedObject4.SELECT_WITH_LIMIT,
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
		connectionParam.executeStatement(MappedObject16.SELECT_WITH_LIMIT,
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
	
	
	public static void main(String[] args) {
		
	}

}
