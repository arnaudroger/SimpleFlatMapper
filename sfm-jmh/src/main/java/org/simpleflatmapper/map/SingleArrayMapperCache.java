package org.simpleflatmapper.map;

import org.simpleflatmapper.map.mapper.MapperKey;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public final class ArrayMapperCache<K extends FieldKey<K>, M> implements IMapperCache<K, M> {

	@SuppressWarnings("unchecked")
	private final AtomicReference<CacheEntry<K, M>[]> mapperCache = new AtomicReference<CacheEntry<K, M>[]>(new CacheEntry[0]);
	
	private static final class CacheEntry<K extends FieldKey<K>, M> {
		final MapperKey<K> key;
		final M mapper;
		CacheEntry(final MapperKey<K> key, final M mapper) {
			this.key = key;
			this.mapper = mapper;
		}

        @Override
        public String toString() {
            return "{" +  key +
                    "," + mapper +
                    '}';
        }
    }
	@Override
	@SuppressWarnings("unchecked")
	public void add(final MapperKey<K> key, final M mapper) {
		CacheEntry<K, M>[] entries;
		CacheEntry<K, M>[] newEntries;
		do {
			entries = mapperCache.get();

            for (CacheEntry<K, M> entry : entries) {
                if (entry.key.equals(key)) {
                    // already added
                    return;
                }
            }
			
			newEntries = new CacheEntry[entries.length + 1];
			
			System.arraycopy(entries, 0, newEntries, 0, entries.length);
			newEntries[entries.length] = new CacheEntry<K, M>(key, mapper);
		
		} while(!mapperCache.compareAndSet(entries, newEntries));
	}

	@Override
	public M get(MapperKey<K> key) {
		final CacheEntry<K, M>[] entries = mapperCache.get();
        for (final CacheEntry<K, M> entry : entries) {
            if (entry.key.equals(key)) {
                return entry.mapper;
            }
        }
		return null;
	}

	@Override
	public int size() {
		return mapperCache.get().length;
	}

	@Override
    public String toString() {
        return "MapperCache{" + Arrays.toString(mapperCache.get()) +
                '}';
    }
}
