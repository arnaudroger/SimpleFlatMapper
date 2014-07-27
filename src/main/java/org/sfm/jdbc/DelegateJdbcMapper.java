package org.sfm.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.sfm.map.Mapper;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.Handler;
import org.sfm.utils.ListHandler;

public final class DelegateJdbcMapper<T> implements JdbcMapper<T> {

	private final Instantiator<T> instantiator;
	private final Mapper<ResultSet, T> delegate;
	
	public DelegateJdbcMapper( Mapper<ResultSet, T> delegate, Instantiator<T> instantiator) {
		this.delegate = delegate;
		this.instantiator = instantiator;
	}

	@Override
	public <H extends Handler<T>> H forEach(ResultSet rs, H handle) throws Exception {
		while(rs.next()) {
			T t = instantiator.newInstance();
			map(rs, t);
			handle.handle(t);
		}
		return handle;
	}
	
	@Override
	public <H extends Handler<T>> H forEach(PreparedStatement statement, H handle)
			throws Exception {
		ResultSet rs = statement.executeQuery();
		try {
			forEach(rs, handle);
		} finally {
			rs.close();
		}
		return handle;
	}

	@Override
	public void map(ResultSet source, T target) throws Exception {
		delegate.map(source, target);
	}

	@Override
	public List<T> list(ResultSet rs) throws Exception {
		return forEach(rs, new ListHandler<T>()).getList();
	}

	@Override
	public List<T> list(PreparedStatement ps) throws Exception {
		return forEach(ps, new ListHandler<T>()).getList();
	}
}
