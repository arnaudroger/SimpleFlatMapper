package org.sfm.jdbc.spring;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.AbstractMapperFactory;
import org.sfm.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.sfm.reflect.TypeReference;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import java.lang.reflect.Type;
import java.util.List;

public final class JdbcTemplateMapperFactory extends AbstractMapperFactory<JdbcColumnKey,
		FieldMapperColumnDefinition<JdbcColumnKey>,
		JdbcTemplateMapperFactory> {


	private JdbcTemplateMapperFactory() {
		super(new FieldMapperColumnDefinitionProviderImpl<JdbcColumnKey>(), FieldMapperColumnDefinition.<JdbcColumnKey>identity());
	}

	public static JdbcTemplateMapperFactory newInstance() {
		return new JdbcTemplateMapperFactory();
	}
	
	public <T> RowMapper<T> newRowMapper(Class<T> target)
			throws MapperBuildingException {
		return newMapper(target);
	}

	public <T> RowMapper<T> newRowMapper(TypeReference<T> target)
			throws MapperBuildingException {
		return newMapper(target);
	}

	public <T> RowMapper<T> newRowMapper(Type target)
			throws MapperBuildingException {
		return newMapper(target);
	}

	public <T> JdbcTemplateMapper<T> newMapper(Class<T> target) {
		return newMapper((Type)target);
	}

	public <T> JdbcTemplateMapper<T> newMapper(Type target) {
		return new JdbcTemplateMapper<T>(this.<T>newJdbcMapper(target));
	}

	private <T> JdbcMapper<T> newJdbcMapper(Type target) {
		return JdbcMapperFactory.newInstance(this).newMapper(target);
	}

	public <T> JdbcTemplateMapper<T> newMapper(TypeReference<T> target) {
		return newMapper(target.getType());
	}

	public <T> PreparedStatementCallback<List<T>> newPreparedStatementCallback(Class<T> target)
			throws MapperBuildingException {
		return newMapper(target);
	}

	public <T> PreparedStatementCallback<List<T>> newPreparedStatementCallback(Type target)
			throws MapperBuildingException {
		return newMapper(target);
	}

	public <T> PreparedStatementCallback<List<T>> newPreparedStatementCallback(TypeReference<T> target)
			throws MapperBuildingException {
		return newMapper(target);
	}

	public <T> ResultSetExtractor<List<T>> newResultSetExtractor(Class<T> target)
			throws MapperBuildingException {
		return newMapper(target);
	}

	public <T> ResultSetExtractor<List<T>> newResultSetExtractor(Type target)
			throws MapperBuildingException {
		return newMapper(target);
	}

	public <T> ResultSetExtractor<List<T>> newResultSetExtractor(TypeReference<T> target)
			throws MapperBuildingException {
		return newMapper(target);
	}

	public <T> SqlParameterSourceFactory<T> newSqlParameterSourceFactory(Class<T> target) {
		return newSqlParameterSourceFactory((Type)target);
	}

	public <T> SqlParameterSourceFactory<T> newSqlParameterSourceFactory(Type target) {
		return new SqlParameterSourceFactory<T>(new DynamicPlaceHolderValueGetterSource<T>(this.<T>getClassMeta(target), mapperConfig()));
	}

	public <T> SqlParameterSourceFactory<T> newSqlParameterSourceFactory(TypeReference<T> target) {
		return newSqlParameterSourceFactory(target.getType());
	}

	public <T> SqlParameterSourceFactory<T> newSqlParameterSourceFactory(Class<T> target, ParsedSql parsedSql) {
		return newSqlParameterSourceFactory((Type)target, parsedSql);
	}

	public <T> SqlParameterSourceFactory<T> newSqlParameterSourceFactory(Type target, ParsedSql parsedSql) {
		SqlParameterSourceBuilder<T> builder = new SqlParameterSourceBuilder<T>(this.<T>getClassMeta(target), mapperConfig());
		return builder.buildFactory(parsedSql);
	}

	public <T> SqlParameterSourceFactory<T> newSqlParameterSourceFactory(TypeReference<T> target, ParsedSql parsedSql) {
		return newSqlParameterSourceFactory(target.getType(), parsedSql);
	}
}
