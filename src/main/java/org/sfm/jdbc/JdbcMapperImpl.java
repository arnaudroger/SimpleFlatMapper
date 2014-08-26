package org.sfm.jdbc;

import java.sql.ResultSet;

import org.sfm.map.FieldMapper;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.Handler;

public final class JdbcMapperImpl<T> implements JdbcMapper<T> {

	
	private final FieldMapper<ResultSet, T>[] fieldMappers;
	private final Instantiator<ResultSet, T> instantiator;
	
	public JdbcMapperImpl(final FieldMapper<ResultSet, T>[] mappers, final Instantiator<ResultSet, T> instantiator) {
		this.fieldMappers = mappers;
		this.instantiator = instantiator;
	}

	@Override
	public T map(final ResultSet source) throws Exception {
		final T target = instantiator.newInstance(source);
		for(int i = 0; i < fieldMappers.length; i++) {
			fieldMappers[i].map(source, target);
		}
		return target;
	}
	
	@Override
	public <H extends Handler<T>> H forEach(final ResultSet rs, final H handler)
			throws Exception {
		while(rs.next()) {
			T t = map(rs);
			handler.handle(t);
		}
		return handler;
	}

}
