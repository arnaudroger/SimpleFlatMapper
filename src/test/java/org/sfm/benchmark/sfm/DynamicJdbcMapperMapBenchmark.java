package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.benchmark.BenchmarkRunner;
import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.benchmark.SysOutBenchmarkListener;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.asm.AsmFactory;

public class DynamicJdbcMapperMapBenchmark extends AbstractMapperQueryExecutor implements QueryExecutor {
	
	Instantiator<DbObject> instantiator;
	
	public DynamicJdbcMapperMapBenchmark(Connection conn) throws NoSuchMethodException, SecurityException, SQLException {
		super(JdbcMapperFactory.newInstance().newMapper(DbObject.class), conn);
		instantiator = new InstantiatorFactory(new AsmFactory()).getInstantiator(DbObject.class);
	}
	
	@Override
	public final void forEach(final ForEachListener ql) throws Exception {
		while(rs.next()) {
			DbObject o = instantiator.newInstance();
			mapper.map(rs, o);
			ql.object(o);
		}
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException, Exception {
		new BenchmarkRunner(-1, new DynamicJdbcMapperMapBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(DynamicJdbcMapperMapBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new DynamicJdbcMapperMapBenchmark(DbHelper.benchmarkDb())).run(new SysOutBenchmarkListener(DynamicJdbcMapperMapBenchmark.class, "SmallQuery"));
	}

}
