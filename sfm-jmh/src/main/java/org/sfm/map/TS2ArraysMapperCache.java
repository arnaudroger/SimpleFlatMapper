package org.sfm.map;

import org.sfm.map.mapper.MapperKey;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

public final class TS2ArraysMapperCache<K extends FieldKey<K>, M> implements IMapperCache<K, M> {

	private static final int SIZE_THRESHOLD = 32;
	private final Comparator<MapperKey<K>> comparator;
	@SuppressWarnings("unchecked")
	private final AtomicReference<Entries<K>> mapperCache = new AtomicReference<Entries<K>>(new Entries<K>(0, false));

	public TS2ArraysMapperCache(Comparator<MapperKey<K>> comparator) {
		this.comparator = comparator;
	}

	@SuppressWarnings("unchecked")
	public void add(final MapperKey<K> key, final M mapper) {
		Entries<K> entries;
		Entries<K> newEntries;
		do {
			entries = mapperCache.get();

			final int i = findInsertionPoint(key, entries);

			if (i >= 0) {
				if (!key.equals(entries.keys[i])) {
					throw new IllegalStateException("Comparator find key " + key + " to be equal to " + entries.keys[i] + " but is not");
				}
				return;
			}

			int insertionPoint = -1 - i;

			final boolean bSearch = comparator != null && (entries.keys.length + 1) > SIZE_THRESHOLD;
			newEntries = insertEntry(key, mapper, entries, insertionPoint,
					bSearch);

		} while(!mapperCache.compareAndSet(entries, newEntries));
	}

	private Entries<K> insertEntry(MapperKey<K> key, M mapper, Entries<K> entries, int insertionPoint, boolean bSearch) {
		Entries<K> newEntries = new Entries<>(entries.keys.length + 1, bSearch);

		System.arraycopy(entries.keys, 0, newEntries.keys, 0, insertionPoint);
		System.arraycopy(entries.values, 0, newEntries.values, 0, insertionPoint);

		newEntries.keys[insertionPoint] = key;
		newEntries.values[insertionPoint] = mapper;

		System.arraycopy(entries.keys, insertionPoint, newEntries.keys, insertionPoint + 1, entries.keys.length - insertionPoint);
		System.arraycopy(entries.values, insertionPoint, newEntries.values, insertionPoint + 1, entries.keys.length - insertionPoint);
		return newEntries;
	}

	public M get(MapperKey<K> key) {
		return bSearch(key, mapperCache.get());
	}

	@Override
	public int size() {
		return mapperCache.get().keys.length;
	}

	private M bSearch(MapperKey<K> key, Entries<K> entries) {
		final int i = findKey(key, entries);
		if (i >= 0) {
            return (M) entries.values[i];
        }
		return null;
	}

	private int findInsertionPoint(MapperKey<K> key, Entries<K> entries) {
		if (comparator == null) {
			return iFindKey(key, entries.keys);
		} else {
			return Arrays.binarySearch(entries.keys, key, comparator);
		}
	}

	private int findKey(MapperKey<K> key, Entries<K> entries) {
		if (entries.bsearch) {
			return Arrays.binarySearch(entries.keys, key, comparator);
		} else {
			return iFindKey(key, entries.keys);
		}
	}

	private int iFindKey(MapperKey<K> key, MapperKey<K>[] keys) {
		for(int i = 0; i < keys.length; i++) {
			if (key.equals(keys[i])) {
				return i;
			}
		}
		return - keys.length - 1;
	}


	private static class Entries<K extends FieldKey<K>> {
		private final MapperKey<K>[] keys;
		private final Object[] values;
		private final boolean bsearch;

		Entries(int size, boolean bsearch) {
			this.keys = new MapperKey[size];
			this.values = new Object[size];
			this.bsearch = bsearch;
		}
	}

	@Override
	public String toString() {
		return "Sorted2ArraysMapperCache{" +
				"mapperCache=" + Arrays.toString(mapperCache.get().keys) +
				'}';
	}
}
