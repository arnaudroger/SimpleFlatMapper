package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.simpleflatmapper.util.TypeReference;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.util.List;

public final class JdbcTemplateMapperFactory extends AbstractMapperFactory<JdbcColumnKey, JdbcTemplateMapperFactory> {


	private JdbcTemplateMapperFactory() {
		super(new FieldMapperColumnDefinitionProviderImpl<JdbcColumnKey>(), FieldMapperColumnDefinition.<JdbcColumnKey>identity());
	}

	public static JdbcTemplateMapperFactory newInstance() {
		return new JdbcTemplateMapperFactory();
	}
	
	public <T> RowMapperImpl<T> newRowMapper(Class<T> target)
			throws MapperBuildingException {
		return newRowMapper((Type)target);
	}

	public <T> RowMapperImpl<T> newRowMapper(TypeReference<T> target)
			throws MapperBuildingException {
		return newRowMapper(target.getType());
	}

	public <T> RowMapperImpl<T> newRowMapper(Type target)
			throws MapperBuildingException {
		return new RowMapperImpl<T>(this.<T>newJdbcMapper(target));
	}

	public <T> JdbcMapper<T> newJdbcMapper(Type target) {
		return JdbcMapperFactory.newInstance(this).newMapper(target);
	}

	public <T> PreparedStatementCallbackImpl<T> newPreparedStatementCallback(Class<T> target)
			throws MapperBuildingException {
		return newPreparedStatementCallback((Type)target);
	}

	public <T> PreparedStatementCallbackImpl<T> newPreparedStatementCallback(Type target)
			throws MapperBuildingException {
		return new PreparedStatementCallbackImpl<T>(this.<T>newJdbcMapper(target));
	}

	public <T> PreparedStatementCallbackImpl<T> newPreparedStatementCallback(TypeReference<T> target)
			throws MapperBuildingException {
		return newPreparedStatementCallback(target.getType());
	}

	public <T> ResultSetExtractorImpl<T> newResultSetExtractor(Class<T> target)
			throws MapperBuildingException {
		return newResultSetExtractor((Type)target);
	}

	public <T> ResultSetExtractorImpl<T> newResultSetExtractor(Type target)
			throws MapperBuildingException {
		return new ResultSetExtractorImpl<T>(this.<T>newJdbcMapper(target));
	}

	public <T> ResultSetExtractorImpl<T> newResultSetExtractor(TypeReference<T> target)
			throws MapperBuildingException {
		return newResultSetExtractor(target.getType());
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

	public <T> SqlParameterSourceFactory<T> newSqlParameterSourceFactory(Class<T> target, String sql) {
		return newSqlParameterSourceFactory((Type)target, sql);
	}

	public <T> SqlParameterSourceFactory<T> newSqlParameterSourceFactory(Type target, String sql) {
		SqlParameterSourceBuilder<T> builder = new SqlParameterSourceBuilder<T>(this.<T>getClassMeta(target), mapperConfig());
		return builder.buildFactory(sql);
	}

	public <T> SqlParameterSourceFactory<T> newSqlParameterSourceFactory(TypeReference<T> target, String sql) {
		return newSqlParameterSourceFactory(target.getType(), sql);
	}

	public <T, K> JdbcTemplateCrudDSL<T, K> crud(Type target, Type keyTarget) {
		return new JdbcTemplateCrudDSL<T, K>(this, target, keyTarget);
	}

	public <T, K> JdbcTemplateCrudDSL<T, K> crud(Class<T> target, Class<K> keyTarget) {
		return crud((Type)target, (Type)keyTarget);
	}

	public <T> MappingSqlQuery<T> mappingSqlQuery(Type target, DataSource ds, String sql) {
		return new MappingSqlQuery<T>(ds, sql, this.<T>newJdbcMapper(target));
	}
}
