package org.sfm.jdbc.spring;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.jdbc.JdbcMapper;
import org.springframework.jdbc.core.RowMapper;

public final class RowMapperDelegate<T> implements RowMapper<T> {
	private final JdbcMapper<T> mapper;
	
	public RowMapperDelegate(JdbcMapper<T> mapper) {
		this.mapper = mapper;
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		return mapper.map(rs);
	}
}
