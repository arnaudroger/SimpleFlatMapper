package org.sfm.jdbc;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.sfm.map.AbstractMapperBuilderImpl;
import org.sfm.map.FieldMapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

public final class ResultSetMapperBuilderImpl<T> extends AbstractMapperBuilderImpl<ResultSet, T, ColumnKey, JdbcMapper<T>, ResultSetMapperBuilder<T>> implements ResultSetMapperBuilder<T> {

	private final Map<String, String> aliases;
	private int columnIndex = 1;
	protected final Map<String, FieldMapper<ResultSet, ?>> customMappings;
	private JdbcMapperErrorHandler jdbcMapperErrorHandler = new RethrowJdbcMapperErrorHandler();

	public ResultSetMapperBuilderImpl(final Type target) throws MapperBuildingException {
		this(target, new ReflectionService());
	}
	public ResultSetMapperBuilderImpl(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(target, reflectService, null, null);
	}
	@SuppressWarnings("unchecked")
	public ResultSetMapperBuilderImpl(final Type target, ReflectionService reflectService, final Map<String,String> aliases, Map<String, FieldMapper<ResultSet, ?>> customMappings) throws MapperBuildingException {
		this(target, (ClassMeta<T>) reflectService.getClassMeta(target), aliases, customMappings);
	}
	
	public ResultSetMapperBuilderImpl(final Type target, final ClassMeta<T> classMeta, final Map<String,String> aliases, Map<String, FieldMapper<ResultSet, ?>> customMappings) throws MapperBuildingException {
		super(target, ResultSet.class, classMeta, new ResultSetGetterFactory(), new ResultSetFieldMapperFactory(classMeta.getReflectionService().getSetterFactory(), new ResultSetGetterFactory()));
		this.aliases = aliases;
		this.customMappings = customMappings;
	}


	@Override
	public final ResultSetMapperBuilder<T> addNamedColumn(final String column) {
		return addNamedColumn(column, ResultSetGetterFactory.UNDEFINED);
	}

	@Override
	public final ResultSetMapperBuilder<T> addIndexedColumn(final String column) {
		return addIndexedColumn(column, columnIndex ++);
	}

	@Override
	public final ResultSetMapperBuilder<T> addIndexedColumn(final String column, final int columnIndex) {
		return addIndexedColumn(column, columnIndex, ResultSetGetterFactory.UNDEFINED);
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String property, final String column) {
		return addMapping(property, column, ResultSetGetterFactory.UNDEFINED);
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String property, final int column) {
		return addMapping(property, column, ResultSetGetterFactory.UNDEFINED);
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addNamedColumn(final String column, final int sqlType) {
		return addMapping(columnToPropertyName(column), column, sqlType);
	}
	
	private String columnToPropertyName(String column) {
		if (aliases == null || aliases.isEmpty()) {
			return column;
		} 
		String alias = aliases.get(column.toUpperCase());
		if (alias == null) {
			return column;
		}
		return alias;
	}
	@Override
	public final ResultSetMapperBuilder<T> addIndexedColumn(final String column, final int columnIndex, final int sqlType) {
		return addMapping(columnToPropertyName(column), new ColumnKey(column, columnIndex, sqlType));
	}

	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String propertyName, final int columnIndex, final int sqlType) {
		return addMapping(propertyName, new ColumnKey("column:"+ columnIndex, columnIndex, sqlType));
	}
	
	@Override
	public ResultSetMapperBuilder<T> addMapping(String propertyName, String column,
			int sqlType) {
		return addMapping(propertyName, new ColumnKey(column, -1, sqlType));
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addMapping(final ResultSetMetaData metaData) throws SQLException {
		for(int i = 1; i <= metaData.getColumnCount(); i++) {
			addIndexedColumn(metaData.getColumnLabel(i), i, metaData.getColumnType(i));
		}
		
		return this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected FieldMapper<ResultSet, T> getCustomMapper(final ColumnKey columnKey) {
		return customMappings != null ? (FieldMapper<ResultSet, T>) customMappings.get(columnKey.getColumnName().toUpperCase()) : null;
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
	@Override
	protected MapperBuilder<ResultSet, T, ColumnKey, ?, ?> newMapperBuilder(Type type, ClassMeta<T> classMeta) {
		return new  ResultSetMapperBuilderImpl<T>(type, classMeta, aliases, customMappings);
	}

}