package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.ResultSet;
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

public class DynamicJdbcMapperMapBenchmark<T> extends AbstractMapperQueryExecutor<T> implements QueryExecutor {
	
	Instantiator<T> instantiator;
	
	public DynamicJdbcMapperMapBenchmark(Connection conn, Class<T> target) throws NoSuchMethodException, SecurityException, SQLException {
		super(JdbcMapperFactory.newInstance().newMapper(target), conn, target);
		instantiator = new InstantiatorFactory(new AsmFactory()).getInstantiator(target);
	}
	
	@Override
	protected final void forEach(ResultSet rs, final ForEachListener ql) throws Exception {
		while(rs.next()) {
			T o = instantiator.newInstance();
			mapper.map(rs, o);
			ql.object(o);
		}
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException, Exception {
		new BenchmarkRunner(-1, new DynamicJdbcMapperMapBenchmark<DbObject>(DbHelper.benchmarkDb(), DbObject.class)).run(new SysOutBenchmarkListener(DynamicJdbcMapperMapBenchmark.class, "BigQuery"));
		new BenchmarkRunner(1, new DynamicJdbcMapperMapBenchmark<DbObject>(DbHelper.benchmarkDb(), DbObject.class)).run(new SysOutBenchmarkListener(DynamicJdbcMapperMapBenchmark.class, "SmallQuery"));
	}

}
