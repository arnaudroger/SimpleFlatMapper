package org.sfm.map;

import java.util.concurrent.atomic.AtomicReference;

public final class MapperCache<M> {

	@SuppressWarnings("unchecked")
	private final AtomicReference<CacheEntry<M>[]> mapperCache = new AtomicReference<CacheEntry<M>[]>(new CacheEntry[0]);
	
	private static final class CacheEntry<M> {
		final MapperKey key;
		final M mapper;
		CacheEntry(final MapperKey key, final M mapper) {
			this.key = key;
			this.mapper = mapper;
		}
	}
	@SuppressWarnings("unchecked")
	public void add(final MapperKey key, final M mapper) {
		CacheEntry<M>[] entries;
		CacheEntry<M>[] newEntries;
		do {
			entries = mapperCache.get();
			
			for(int i = 0; i < entries.length; i++) {
				if (entries[0].key.equals(key)) {
					// already added 
					return;
				}
			}
			
			newEntries = new CacheEntry[entries.length + 1];
			
			System.arraycopy(entries, 0, newEntries, 0, entries.length);
			newEntries[entries.length] = new CacheEntry<M>(key, mapper);
		
		} while(!mapperCache.compareAndSet(entries, newEntries));
	}

	public M get(MapperKey key) {
		final CacheEntry<M>[] entries = mapperCache.get();
		for(int i = 0; i < entries.length; i++) {
			final CacheEntry<M> entry = entries[i];
			if (entry.key.equals(key)) {
				return entry.mapper;
			}
		}
		return null;
	}
}
