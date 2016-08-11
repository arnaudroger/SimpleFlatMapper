package org.simpleflatmapper.map;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.map.mapper.MapperKey;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class SortedMapperCache<K extends FieldKey<K>, M> implements IMapperCache<K, M> {

    private static final int BSEARCH_THRESHOLD = 0;

    private final Comparator<CacheEntry<K, M>> comparator;
    @SuppressWarnings("unchecked")
    private final AtomicReference<CacheEntry<K, M>[]> mapperCache = new AtomicReference<CacheEntry<K, M>[]>(new CacheEntry[0]);

    public SortedMapperCache(Comparator<MapperKey<K>> comparator) {
        this.comparator = comparator == null ? null : new CacheEntryComparator<>(comparator);
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

            Arrays.sort(newEntries, comparator);

        } while(!mapperCache.compareAndSet(entries, newEntries));


    }

    @SuppressWarnings("unchecked")
    private CacheEntry<K, M>[] insertEntry(CacheEntry<K, M> entry, CacheEntry<K, M>[] entries, int insertionPoint) {
        CacheEntry<K, M>[] newEntries = new CacheEntry[entries.length + 1];
        System.arraycopy(entries, 0, newEntries, 0, insertionPoint);
        newEntries[insertionPoint] = entry;
        System.arraycopy(entries, insertionPoint, newEntries, insertionPoint + 1, entries.length - insertionPoint);
        return newEntries;
    }

    public M get(MapperKey<K> key) {
        final CacheEntry<K, M>[] entries = mapperCache.get();

        return bSearch(key, entries);
    }

    @Override
    public int size() {
        return mapperCache.get().length;
    }

    private M bSearch(MapperKey<K> key, CacheEntry<K, M>[] entries) {
        final int i = Arrays.binarySearch(entries, new CacheEntry<K, M>(key, null), comparator);
        if (i >= 0) {
            return entries[i].mapper;
        }
        return null;
    }

    private int findKey(MapperKey<K> key, CacheEntry<K, M>[] entries) {
        if (comparator == null || entries.length < BSEARCH_THRESHOLD) {
            return findKeyIterativeSearch(key, entries);
        } else {
            return findKeyBSearch(key, entries);
        }
    }

    private int findKeyIterativeSearch(MapperKey<K> key, CacheEntry<K, M>[] entries) {
        return - entries.length - 1;
    }

    private int findKeyBSearch(MapperKey<K> key, CacheEntry<K, M>[] entries) {
        return Arrays.binarySearch(entries, new CacheEntry<K, M>(key, null), comparator);
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

    public static void main(String[] args) {
        final IMapperCache<JdbcColumnKey, Object> mapperCache = CacheType.SARRAY.newCache();
        final IMapperCache<JdbcColumnKey, Object> mapperCache2 = CacheType.TS2ARRAY.newCache();


        final List<MapperKey<JdbcColumnKey>> mapperKeys = Utils.generateKeys(30, 10);
        final Map<MapperKey<JdbcColumnKey>, Object> objects = new HashMap<>();

        for(MapperKey<JdbcColumnKey> key : mapperKeys) {
            Object o = objects.getOrDefault(key, new Object());
            mapperCache.add(key, o);
            mapperCache2.add(key, o);
            objects.put(key, o);
        }

        System.out.println("objects = " + objects.size());
        System.out.println("mapperCache = " + mapperCache.size());
        System.out.println("mapperCache2 = " + mapperCache2.size());

        for(MapperKey<JdbcColumnKey> key : mapperKeys) {
            Object o = objects.get(key);
            if (o != mapperCache.get(key)) {
                throw new IllegalArgumentException();
            }
            if (o != mapperCache2.get(key)) {
                throw new IllegalArgumentException();
            }
        }


    }
}
