package org.sfm.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sfm.map.Mapper;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.Handler;

public final class ResultSetMapperImpl<T> implements ResultSetMapper<T> {

	private final Instantiator<T> instantiator;
	private final Mapper<ResultSet, T> delegate;
	
	public ResultSetMapperImpl( Mapper<ResultSet, T> delegate, Instantiator<T> instantiator) {
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
}
