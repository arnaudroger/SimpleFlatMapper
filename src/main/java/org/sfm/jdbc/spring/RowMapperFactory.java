package org.sfm.jdbc.spring;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.springframework.jdbc.core.RowMapper;

public final class RowMapperFactory {
	private final JdbcMapperFactory jdbcMapperFactory = new JdbcMapperFactory();

	public RowMapperFactory fieldMapperErrorHandler(FieldMapperErrorHandler fieldMapperErrorHandler) {
		jdbcMapperFactory.fieldMapperErrorHandler(fieldMapperErrorHandler);
		return this;
	}

	public RowMapperFactory mapperBuilderErrorHandler(
			MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		jdbcMapperFactory.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
		return this;
	}

	public RowMapperFactory useAsm(boolean useAsm) {
		jdbcMapperFactory.useAsm(useAsm);
		return this;
	}

	public <T> RowMapper<T> newMapper(Class<T> target,	ResultSetMetaData metaData) throws SQLException,
			MapperBuildingException {
		return new RowMapperDelegate<>(jdbcMapperFactory.newMapper(target, metaData));
	}
	public <T> RowMapper<T> newMapper(Class<T> target)
			throws MapperBuildingException {
		return new RowMapperDelegate<>(jdbcMapperFactory.newMapper(target));
	}
}
