package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.beans.SmallBenchmarkObject;
import org.sfm.benchmark.BenchmarkRunner;
import org.sfm.benchmark.SysOutBenchmarkListener;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.DelegateJdbcMapper;
import org.sfm.jdbc.ResultSetMapperBuilder;
import org.sfm.reflect.Instantiator;

public class StaticJdbcMapperBenchmark<T> extends ForEachMapperQueryExecutor<T> {
	public StaticJdbcMapperBenchmark(Connection conn, Class<T> target) throws NoSuchMethodException, SecurityException, SQLException {
		super(newMapper(target), conn, target);
	}

	@SuppressWarnings("unchecked")
	private static <T> DelegateJdbcMapper<T> newMapper(Class<T> target) {
		if (target.equals(DbObject.class)) {
			return (DelegateJdbcMapper<T>) 
					new DelegateJdbcMapper<DbObject>(
							new ResultSetMapperBuilder<DbObject>(DbObject.class)
								.addIndexedColumn("id")
								.addIndexedColumn("name")
								.addIndexedColumn("email")
								.addIndexedColumn("creation_time").mapper(), 
					new Instantiator<DbObject>() {
						@Override
						public DbObject newInstance() throws Exception {
							return new DbObject();
						}
					});
		} else if (target.equals(SmallBenchmarkObject.class)) {
			return (DelegateJdbcMapper<T>) 
					new DelegateJdbcMapper<SmallBenchmarkObject>(
							new ResultSetMapperBuilder<SmallBenchmarkObject>(SmallBenchmarkObject.class)
								.addIndexedColumn("id")
								.addIndexedColumn("name")
								.addIndexedColumn("email")
								.addIndexedColumn("year_started").mapper(), 
					new Instantiator<SmallBenchmarkObject>() {
						@Override
						public SmallBenchmarkObject newInstance() throws Exception {
							return new SmallBenchmarkObject();
						}
					});
		} else {
			throw new UnsupportedOperationException(target.getName());
		}
	}
}
