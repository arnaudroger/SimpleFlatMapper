package org.sfm.map;

import org.sfm.map.mapper.MapperKey;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

public final class SortedMapperCache2<K extends FieldKey<K>, M> {

    private final Comparator<CacheEntry<K, M>> comparator;
    @SuppressWarnings("unchecked")
    private final AtomicReference<CacheEntry<K, M>[]> mapperCache = new AtomicReference<CacheEntry<K, M>[]>(new CacheEntry[0]);

    public SortedMapperCache2(Comparator<MapperKey<K>> comparator) {
        this.comparator = new CacheEntryComparator<>(comparator);
    }

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

            Arrays.sort(entries, comparator);

        } while(!mapperCache.compareAndSet(entries, newEntries));
    }

    public M get(MapperKey<K> key) {
        final CacheEntry<K, M>[] entries = mapperCache.get();
        return bSearch(key, entries);
    }

    private M bSearch(MapperKey<K> key, CacheEntry<K, M>[] entries) {
        final int i = Arrays.binarySearch(entries, new CacheEntry<K, M>(key, null), comparator);
        if (i >= 0) {
            return entries[i].mapper;
        }
        return null;
    }

    @Override
    public String toString() {
        return "SortedMapperCache{" + Arrays.toString(mapperCache.get()) +
                '}';
    }

    private static class CacheEntryComparator<K extends FieldKey<K>, M> implements Comparator<CacheEntry<K, M>> {
        private final Comparator<MapperKey<K>> comparator;

        public CacheEntryComparator(Comparator<MapperKey<K>> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(CacheEntry<K, M> o1, CacheEntry<K, M> o2) {
            return comparator.compare(o1.key, o2.key);
        }
    }
}
