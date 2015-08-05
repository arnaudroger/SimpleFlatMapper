package org.simpleflatmapper.sfm;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.reflect.asm.AsmHelper;
import org.sfm.utils.RowHandler;
import org.simpleflatmapper.beans.SmallBenchmarkObject;
import org.simpleflatmapper.db.ConnectionParam;
import org.simpleflatmapper.jdbc.JdbcManualBenchmark;
import org.simpleflatmapper.param.LimitParam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@State(Scope.Benchmark)
public class JdbcSfmDynamicBenchmark {

	private JdbcMapper<SmallBenchmarkObject> mapper;
	@Setup
	public void init() {
		if (! AsmHelper.isAsmPresent()) {
			throw new RuntimeException("Asm not present or incompatible");
		}
		
		mapper = JdbcMapperFactory.newInstance().newMapper(SmallBenchmarkObject.class);
	}

	@Benchmark
	public void testQuery(ConnectionParam connectionParam, LimitParam limitParam, final Blackhole blackhole) throws Exception {
		Connection conn = connectionParam.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(JdbcManualBenchmark.SELECT_BENCHMARK_OBJ_WITH_LIMIT);
			try {
				ps.setInt(1, limitParam.limit);
				ResultSet rs = ps.executeQuery();
				mapper.forEach(rs, new RowHandler<SmallBenchmarkObject>() {
					@Override
					public void handle(SmallBenchmarkObject smallBenchmarkObject) throws Exception {
						blackhole.consume(smallBenchmarkObject);
					}
				});
			}finally {
				ps.close();
			}
		}finally {
			conn.close();
		}
	}
}
