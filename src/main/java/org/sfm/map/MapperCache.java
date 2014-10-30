package org.sfm.map;

import java.util.concurrent.atomic.AtomicReference;

public final class MapperCache<K, M> {

	@SuppressWarnings("unchecked")
	private final AtomicReference<CacheEntry<K, M>[]> mapperCache = new AtomicReference<CacheEntry<K, M>[]>(new CacheEntry[0]);
	
	private static final class CacheEntry<K, M> {
		final K key;
		final M mapper;
		CacheEntry(final K key, final M mapper) {
			this.key = key;
			this.mapper = mapper;
		}
	}
	@SuppressWarnings("unchecked")
	public void add(final K key, final M mapper) {
		CacheEntry<K, M>[] entries;
		CacheEntry<K, M>[] newEntries;
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
			newEntries[entries.length] = new CacheEntry<K, M>(key, mapper);
		
		} while(!mapperCache.compareAndSet(entries, newEntries));
	}

	public M get(K key) {
		final CacheEntry<K, M>[] entries = mapperCache.get();
		for(int i = 0; i < entries.length; i++) {
			final CacheEntry<K, M> entry = entries[i];
			if (entry.key.equals(key)) {
				return entry.mapper;
			}
		}
		return null;
	}
}
