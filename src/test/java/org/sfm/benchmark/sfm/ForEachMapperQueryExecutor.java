package org.sfm.benchmark.sfm;

import java.sql.Connection;
import java.sql.ResultSet;

import org.sfm.beans.DbObject;
import org.sfm.benchmark.ForEachListener;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.utils.Handler;

public class ForEachMapperQueryExecutor extends AbstractMapperQueryExecutor {


	public ForEachMapperQueryExecutor(JdbcMapper<DbObject> mapper,
			Connection conn) {
		super(mapper, conn);
	}

	@Override
	protected final void forEach(ResultSet rs, final ForEachListener ql) throws Exception {
		mapper.forEach(rs, new Handler<DbObject>() {
			@Override
			public void handle(DbObject t) throws Exception {
				ql.object(t);
			}
		});
	}
}