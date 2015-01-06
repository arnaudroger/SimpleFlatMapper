package org.sfm.jdbc.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperBuilder;
import org.sfm.jdbc.SQLMappingException;
import org.sfm.map.*;
import org.sfm.map.impl.ColumnsMapperKey;
import org.sfm.map.impl.FieldMapper;
import org.sfm.map.impl.MapperCache;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
import org.sfm.utils.RowHandler;

public final class DynamicJdbcMapper<T> implements JdbcMapper<T> {

	private final ClassMeta<T> classMeta;
	private final Class<T> target;


	private final FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler;

	private final  MapperBuilderErrorHandler mapperBuilderErrorHandler;
	private final Map<String, String> aliases;
	private final PropertyNameMatcherFactory propertyNameMatcherFactory;
	private final RowHandlerErrorHandler rowHandlerErrorHandler;
	private Map<String, FieldMapper<ResultSet, ?>> customMappings = new HashMap<String, FieldMapper<ResultSet, ?>>();
	private MapperCache<ColumnsMapperKey, JdbcMapper<T>> mapperCache = new MapperCache<ColumnsMapperKey, JdbcMapper<T>>();

	public DynamicJdbcMapper(final Class<T> target, final ClassMeta<T> classMeta,
							 final FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler,
							 final MapperBuilderErrorHandler mapperBuilderErrorHandler,
							 RowHandlerErrorHandler rowHandlerErrorHandler, final Map<String, String> aliases,
							 final Map<String, FieldMapper<ResultSet, ?>> customMappings, PropertyNameMatcherFactory propertyNameMatcherFactory) {
		this.classMeta = classMeta;
		this.target = target;
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		this.aliases = aliases;
		this.customMappings = customMappings;
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		this.rowHandlerErrorHandler = rowHandlerErrorHandler;
	}
	

	@Override
	public final T map(final ResultSet source) throws MappingException {
		try {
			final JdbcMapper<T> mapper = buildMapper(source.getMetaData());
			return mapper.map(source);
		} catch(SQLException e) {
			throw new SQLMappingException(e.getMessage(), e);
		}
	}

	@Override
	public final <H extends RowHandler<T>> H forEach(final ResultSet rs, final H handle)
			throws SQLException, MappingException {
		final JdbcMapper<T> mapper = buildMapper(rs.getMetaData());
		return mapper.forEach(rs, handle);
	}
	
	@Override
	public final Iterator<T> iterate(final ResultSet rs)
			throws SQLException, MappingException {
		final JdbcMapper<T> mapper = buildMapper(rs.getMetaData());
		return mapper.iterate(rs);
	}
	
	//IFJAVA8_START
	@Override
	public Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		final JdbcMapper<T> mapper = buildMapper(rs.getMetaData());
		return mapper.stream(rs);
	}
	//IFJAVA8_END

	public JdbcMapper<T> buildMapper(final ResultSetMetaData metaData) throws MapperBuildingException, SQLException {
		
		final ColumnsMapperKey key = mapperKey(metaData);
		
		JdbcMapper<T> mapper = mapperCache.get(key);
		
		if (mapper == null) {
			final JdbcMapperBuilder<T> builder = new JdbcMapperBuilder<T>(target, classMeta, aliases, customMappings, propertyNameMatcherFactory);

			builder.jdbcMapperErrorHandler(rowHandlerErrorHandler);
			builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
			builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
			builder.addMapping(metaData);
			
			mapper = builder.mapper();
			mapperCache.add(key, mapper);
		}
		return mapper;
	}
	
	private static ColumnsMapperKey mapperKey(final ResultSetMetaData metaData) throws SQLException {
		final String[] columns = new String[metaData.getColumnCount()];
		
		for(int i = 0; i < columns.length; i++) {
			columns[i] = metaData.getColumnLabel(i + 1);
		}
		
		return new ColumnsMapperKey(columns);
	}



}
