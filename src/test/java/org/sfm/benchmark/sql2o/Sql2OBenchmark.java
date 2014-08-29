package org.sfm.benchmark.sql2o;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;

import org.sfm.benchmark.ForEachListener;
import org.sfm.benchmark.JDBCHelper;
import org.sfm.benchmark.QueryExecutor;
import org.sfm.benchmark.SingleConnectionDataSource;
import org.sql2o.Sql2o;

public class Sql2OBenchmark implements QueryExecutor {

	private Sql2o sql2o;
	private Class<?> target;
	public Sql2OBenchmark(final Connection conn, Class<?> target)  {
		Connection connProxy = (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Connection.class} , new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				if (method.getName().equals("close")) {
					return null;
				}
				return method.invoke(conn, args);
			}
		});
		sql2o = new Sql2o(new SingleConnectionDataSource(connProxy));
		
		this.target = target;

	}
	@Override
	public void forEach(final ForEachListener ql, int limit) throws Exception {
		try (org.sql2o.Connection conn = sql2o.open()) {
			List<?> list = conn.createQuery(JDBCHelper.query(target, limit)).addColumnMapping("YEAR_STARTED", "yearStarted").executeAndFetch(target);
			for(Object o : list) {
				ql.object(o);
			}
		}
	}
	
}
