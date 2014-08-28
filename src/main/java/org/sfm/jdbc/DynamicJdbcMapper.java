package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.MappingException;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.Handler;

public final class DynamicJdbcMapper<T> implements JdbcMapper<T> {

	private final Map<String, Setter<T, Object>> setters;
	
	private final SetterFactory setterFactory;
	private final Class<T> target;
	
	@SuppressWarnings("unchecked")
	private final AtomicReference<CacheEntry<T>[]> mapperCache = new AtomicReference<CacheEntry<T>[]>(new CacheEntry[0]);

	private FieldMapperErrorHandler fieldMapperErrorHandler;

	private MapperBuilderErrorHandler mapperBuilderErrorHandler;

	public DynamicJdbcMapper(final Class<T> target, final SetterFactory setterFactory, 
			final FieldMapperErrorHandler fieldMapperErrorHandler, 
			final MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		this.setterFactory = setterFactory;
		this.setters = setterFactory.getAllSetters(target);
		this.target = target;
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
	}
	
	private static final class CacheEntry<T> {
		final MapperKey key;
		final JdbcMapper<T> mapper;
		public CacheEntry(final MapperKey key, final JdbcMapper<T> mapper) {
			this.key = key;
			this.mapper = mapper;
		}
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
	public final <H extends Handler<T>> H forEach(final ResultSet rs, final H handle)
			throws SQLException, MappingException {
		final JdbcMapper<T>mapper = buildMapper(rs.getMetaData());
		return mapper.forEach(rs, handle);
	}

	private JdbcMapper<T> buildMapper(final ResultSetMetaData metaData) throws MapperBuildingException, SQLException {
		
		final MapperKey key = MapperKey.valueOf(metaData);
		
		JdbcMapper<T> mapper = getMapper(key);
		
		if (mapper == null) {
			final CachedResultSetMapperBuilder<T> builder = new CachedResultSetMapperBuilder<T>(target, setters, setterFactory, AsmHelper.isAsmPresent());
			
			builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
			builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
			builder.addMapping(metaData);
			
			mapper = builder.mapper();
			
			addToMapperCache(new CacheEntry<>(key, mapper));
		}
		return mapper;
	}
	
	@SuppressWarnings("unchecked")
	private void addToMapperCache(final CacheEntry<T> cacheEntry) {
		CacheEntry<T>[] entries;
		CacheEntry<T>[] newEntries;
		do {
			entries = mapperCache.get();
			
			for(int i = 0; i < entries.length; i++) {
				if (entries[0].key.equals(cacheEntry.key)) {
					// already added 
					return;
				}
			}
			
			newEntries = new CacheEntry[entries.length + 1];
			
			System.arraycopy(entries, 0, newEntries, 0, entries.length);
			newEntries[entries.length] = cacheEntry;
		
		} while(!mapperCache.compareAndSet(entries, newEntries));
	}

	protected JdbcMapper<T> getMapper(MapperKey key) {
		final CacheEntry<T>[] entries = mapperCache.get();
		for(int i = 0; i < entries.length; i++) {
			final CacheEntry<T> entry = entries[i];
			if (entry.key.equals(key)) {
				return entry.mapper;
			}
		}
		return null;
	}
}
