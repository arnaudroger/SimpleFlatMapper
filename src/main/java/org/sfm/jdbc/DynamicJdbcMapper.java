package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.MapperCache;
import org.sfm.map.MapperKey;
import org.sfm.map.MappingException;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.RowHandler;

public final class DynamicJdbcMapper<T> implements JdbcMapper<T> {

	private final ClassMeta<T> classMeta;
	private final Class<T> target;


	private final FieldMapperErrorHandler<ColumnKey> fieldMapperErrorHandler;

	private final  MapperBuilderErrorHandler mapperBuilderErrorHandler;
	private final Map<String, String> aliases;
	private Map<String, FieldMapper<ResultSet, ?>> customMappings = new HashMap<String, FieldMapper<ResultSet, ?>>();
	private MapperCache<JdbcMapper<T>> mapperCache = new MapperCache<JdbcMapper<T>>();

	public DynamicJdbcMapper(final Class<T> target, final ReflectionService reflectionService, 
			final FieldMapperErrorHandler<ColumnKey> fieldMapperErrorHandler, 
			final MapperBuilderErrorHandler mapperBuilderErrorHandler, 
			final Map<String, String> aliases, 
			final Map<String, FieldMapper<ResultSet, ?>> customMappings) {
		this.classMeta = reflectionService.getClassMeta(target);
		this.target = target;
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		this.aliases = aliases;
		this.customMappings = customMappings;
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
		final JdbcMapper<T>mapper = buildMapper(rs.getMetaData());
		return mapper.forEach(rs, handle);
	}

	private JdbcMapper<T> buildMapper(final ResultSetMetaData metaData) throws MapperBuildingException, SQLException {
		
		final MapperKey key = mapperKey(metaData);
		
		JdbcMapper<T> mapper = mapperCache.get(key);
		
		if (mapper == null) {
			final ResultSetMapperBuilder<T> builder = new ResultSetMapperBuilderImpl<T>(target, classMeta, aliases, customMappings);
			
			builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
			builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
			builder.addMapping(metaData);
			
			mapper = builder.mapper();
			mapperCache.add(key, mapper);
		}
		return mapper;
	}
	
	private static MapperKey mapperKey(final ResultSetMetaData metaData) throws SQLException {
		final String[] columns = new String[metaData.getColumnCount()];
		
		for(int i = 0; i < columns.length; i++) {
			columns[i] = metaData.getColumnLabel(i + 1);
		}
		
		return new MapperKey(columns);
	}
}
