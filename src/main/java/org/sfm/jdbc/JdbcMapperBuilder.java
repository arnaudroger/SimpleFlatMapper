package org.sfm.jdbc;

import org.sfm.jdbc.impl.JdbcMapperImpl;
import org.sfm.jdbc.impl.ResultSetFieldMapperFactory;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.*;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class JdbcMapperBuilder<T> extends AbstractFieldMapperMapperBuilder<ResultSet, T, JdbcColumnKey>  {

	private int columnIndex = 1;
	private RowHandlerErrorHandler jdbcMapperErrorHandler = new RethrowRowHandlerErrorHandler();

	public JdbcMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}
	public JdbcMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(target, reflectService, new HashMap<String, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>>(), new DefaultPropertyNameMatcherFactory());
	}
	@SuppressWarnings("unchecked")
	public JdbcMapperBuilder(final Type target, ReflectionService reflectService, final Map<String,FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> columnDefinitions, PropertyNameMatcherFactory propertyNameMatcherFactory) throws MapperBuildingException {
		this(target, (ClassMeta<T>) reflectService.getRootClassMeta(target), new RethrowMapperBuilderErrorHandler(), columnDefinitions, propertyNameMatcherFactory);
	}
	
	public JdbcMapperBuilder(final Type target, final ClassMeta<T> classMeta, final MapperBuilderErrorHandler mapperBuilderErrorHandler,  final Map<String,FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> columnDefinitions, PropertyNameMatcherFactory propertyNameMatcherFactory) throws MapperBuildingException {
		super(target, ResultSet.class, classMeta, new ResultSetGetterFactory(), new ResultSetFieldMapperFactory(new ResultSetGetterFactory()), columnDefinitions, propertyNameMatcherFactory, mapperBuilderErrorHandler);
	}

	@Override
	public JdbcMapper<T> mapper() {
		if (reflectionService.isAsmActivated()) {
			try {
				return reflectionService.getAsmFactory().createJdbcMapper(fields(), getInstantiator(), getTargetClass(), jdbcMapperErrorHandler);
			} catch(Exception e) {
				return new JdbcMapperImpl<T>(fields(), getInstantiator(), jdbcMapperErrorHandler);
			}
		} else {
			return new JdbcMapperImpl<T>(fields(), getInstantiator(), jdbcMapperErrorHandler);
		}
	}

	
	public JdbcMapperBuilder<T> addMapping(String column) {
		return addMapping(column, columnIndex++);
	}

	public JdbcMapperBuilder<T> addMapping(final String column, final FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
		return addMapping(column, columnIndex++, columnDefinition);
	}

	public JdbcMapperBuilder<T> addMapping(String column, int index) {
		return addMapping(column, index, JdbcColumnKey.UNDEFINED_TYPE);
	}

	public JdbcMapperBuilder<T> addMapping(String column, int index, final FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
		return addMapping(column, index, JdbcColumnKey.UNDEFINED_TYPE, columnDefinition);
	}


	public JdbcMapperBuilder<T> addMapper(FieldMapper<ResultSet, T> mapper) {
		_addMapper(mapper);
		return this;
	}

	public JdbcMapperBuilder<T> addMapping(final String column, final int columnIndex, final int sqlType) {
		addMapping(column, columnIndex, sqlType, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>identity());
		return this;
	}

	public JdbcMapperBuilder<T> addMapping(final String column, final int columnIndex, final int sqlType, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
		_addMapping(new JdbcColumnKey(column, columnIndex, sqlType), columnDefinition);
		return this;
	}

	public JdbcMapperBuilder<T> addMapping(final ResultSetMetaData metaData) throws SQLException {
		for(int i = 1; i <= metaData.getColumnCount(); i++) {
			addMapping(metaData.getColumnLabel(i), i, metaData.getColumnType(i));
		}
		
		return this;
	}

	
	public JdbcMapperBuilder<T> fieldMapperErrorHandler(FieldMapperErrorHandler<JdbcColumnKey> errorHandler) {
		setFieldMapperErrorHandler(errorHandler);
		return this;
	}
	
	public JdbcMapperBuilder<T> jdbcMapperErrorHandler(RowHandlerErrorHandler jdbcMapperErrorHandler) {
		this.jdbcMapperErrorHandler = jdbcMapperErrorHandler;
		return this;
	}

	@Override
	protected <ST> AbstractFieldMapperMapperBuilder<ResultSet, ST, JdbcColumnKey> newSubBuilder(Type type, ClassMeta<ST> classMeta) {
		return new  JdbcMapperBuilder<ST>(type, classMeta, mapperBuilderErrorHandler, columnDefinitions, propertyNameMatcherFactory);
	}
	

}