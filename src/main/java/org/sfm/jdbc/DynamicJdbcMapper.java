package org.sfm.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.sfm.map.Mapper;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.Handler;
import org.sfm.utils.ListHandler;

public class DynamicJdbcMapper<T> implements JdbcMapper<T> {


	private final Map<String, Setter<T, Object>> setters;
	
	private final Instantiator<T> instantiator;
	private final Class<T> target;
	private final SetterFactory setterFactory;
	@SuppressWarnings("unchecked")
	private final AtomicReference<CacheEntry<T>[]> mapperCache = new AtomicReference<CacheEntry<T>[]>(new CacheEntry[0]);
	
	private static final class CacheEntry<T> {
		final MapperKey key;
		final Mapper<ResultSet, T> mapper;
		public CacheEntry(MapperKey key, Mapper<ResultSet, T> mapper) {
			this.key = key;
			this.mapper = mapper;
		}
	}

	
	public DynamicJdbcMapper(Class<T> target, Instantiator<T> instantiator) {
		this.setterFactory = new SetterFactory();
		this.setters = setterFactory.getAllSetters(target);
		this.instantiator = instantiator;
		this.target = target;
	}

	@Override
	public final void map(ResultSet source, T target) throws Exception {
		final Mapper<ResultSet, T> mapper = buildMapper(source.getMetaData());
		mapper.map(source, target);
	}

	@Override
	public final <H extends Handler<T>> H forEach(final ResultSet rs, final H handle)
			throws Exception {
		
		final Mapper<ResultSet, T> mapper = buildMapper(rs.getMetaData());
		
		while(rs.next()) {
			final T t = instantiator.newInstance();
			mapper.map(rs, t);
			handle.handle(t);
		}
		
		return handle;
	}

	@Override
	public final <H extends Handler<T>> H forEach(PreparedStatement statement, H handle)
			throws Exception {
		ResultSet rs = statement.executeQuery();
		try {
			forEach(rs, handle);
		} finally {
			rs.close();
		}
		return handle;
	}

	private Mapper<ResultSet, T> buildMapper(ResultSetMetaData metaData) throws SQLException {
		
		MapperKey key = MapperKey.valueOf(metaData);
		
		Mapper<ResultSet, T> mapper = getMapper(key);
		
		if (mapper == null) {
			CachedResultSetMapperBuilder<T> builder = new CachedResultSetMapperBuilder<T>(target, setters, setterFactory);
	
			for(int i = 1; i <= metaData.getColumnCount(); i++) {
				builder.addIndexedColumn(metaData.getColumnName(i));
			}
			
			mapper = builder.mapper();
			
			addToMapperCache(new CacheEntry<>(key, mapper));
		}
		return mapper;
	}
	
	@SuppressWarnings("unchecked")
	private void addToMapperCache(CacheEntry<T> cacheEntry) {
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

	private Mapper<ResultSet, T> getMapper(MapperKey key) {
		CacheEntry<T>[] entries = mapperCache.get();
		for(int i = 0; i < entries.length; i++) {
			CacheEntry<T> entry = entries[i];
			if (entry.key.equals(key)) {
				return entry.mapper;
			}
		}
		return null;
	}

	@Override
	public List<T> list(ResultSet rs) throws Exception {
		return forEach(rs, new ListHandler<T>()).getList();
	}

	@Override
	public List<T> list(PreparedStatement ps) throws Exception {
		return forEach(ps, new ListHandler<T>()).getList();
	}
}
