package org.sfm.jdbc;

import java.sql.ResultSet;

import org.sfm.reflect.Getter;

public final class JdbcMapperGetterAdapter<P> implements Getter<ResultSet, P> {

	private final JdbcMapper<P> mapper;
	
	public JdbcMapperGetterAdapter(JdbcMapper<P> mapper) {
		this.mapper = mapper;
	}

	@Override
	public P get(ResultSet target) throws Exception {
		return mapper.map(target);
	}

}
