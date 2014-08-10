package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.ResultSet;

import org.sfm.benchmark.ForEachListener;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.utils.Handler;

public class ForEachMapperQueryExecutor<T> extends AbstractMapperQueryExecutor<T> {


	public ForEachMapperQueryExecutor(JdbcMapper<T> mapper,
			Connection conn, Class<T> target) {
		super(mapper, conn, target);
	}

	@Override
	protected final void forEach(ResultSet rs, final ForEachListener ql) throws Exception {
		mapper.forEach(rs, new Handler<T>() {
			@Override
			public void handle(T t) throws Exception {
				ql.object(t);
			}
		});
	}
}