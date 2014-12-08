package org.sfm.jdbc;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

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
import org.sfm.reflect.meta.PropertyNameMatcher;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;

public final class JdbcMapperBuilder<T> extends AbstractFieldMapperMapperBuilder<ResultSet, T, JdbcColumnKey>  {

	private int columnIndex = 1;
	private RowHandlerErrorHandler jdbcMapperErrorHandler = new RethrowRowHandlerErrorHandler();

	public JdbcMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, new ReflectionService());
	}
	public JdbcMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(target, reflectService, null, null, new DefaultPropertyNameMatcherFactory());
	}
	@SuppressWarnings("unchecked")
	public JdbcMapperBuilder(final Type target, ReflectionService reflectService, final Map<String,String> aliases, Map<String, FieldMapper<ResultSet, ?>> customMappings, PropertyNameMatcherFactory propertyNameMatcherFactory) throws MapperBuildingException {
		this(target, (ClassMeta<T>) reflectService.getClassMeta(target), aliases, customMappings, propertyNameMatcherFactory);
	}
	
	public JdbcMapperBuilder(final Type target, final ClassMeta<T> classMeta, final Map<String,String> aliases, Map<String, FieldMapper<ResultSet, ?>> customMappings, PropertyNameMatcherFactory propertyNameMatcherFactory) throws MapperBuildingException {
		super(target, ResultSet.class, classMeta, new ResultSetGetterFactory(), new ResultSetFieldMapperFactory(new ResultSetGetterFactory()), aliases, customMappings, propertyNameMatcherFactory);
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
	public JdbcMapperBuilder<T> addMapping(String column, int index) {
		return addMapping(column, index, JdbcColumnKey.UNDEFINED_TYPE);
	}
	
	public JdbcMapperBuilder<T> addMapper(FieldMapper<ResultSet, T> mapper) {
		mappers.add(new KeyFieldMapperCouple<ResultSet, T, JdbcColumnKey>(null, mapper));
		return this;
	}
	
	public JdbcMapperBuilder<T> addMapping(final String column, final int columnIndex, final int sqlType) {
		addMapping(new JdbcColumnKey(column, columnIndex, sqlType));
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
	
	public JdbcMapperBuilder<T> mapperBuilderErrorHandler(MapperBuilderErrorHandler errorHandler) {
		setMapperBuilderErrorHandler(errorHandler);
		return this;
	}
	
	
	@Override
	protected <ST> AbstractFieldMapperMapperBuilder<ResultSet, ST, JdbcColumnKey> newSubBuilder(Type type, ClassMeta<ST> classMeta) {
		return new  JdbcMapperBuilder<ST>(type, classMeta, aliases, customMappings, propertyNameMatcherFactory);
	}
	

}