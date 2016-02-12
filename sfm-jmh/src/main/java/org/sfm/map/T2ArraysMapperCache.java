package org.sfm.map;

import org.sfm.map.mapper.MapperKey;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

public final class T2ArraysMapperCache<K extends FieldKey<K>, M> implements IMapperCache<K, M> {

	@SuppressWarnings("unchecked")
	private final AtomicReference<Entries<K>> mapperCache = new AtomicReference<Entries<K>>(new Entries<K>(0));

	public T2ArraysMapperCache() {

	}

	@SuppressWarnings("unchecked")
	public void add(final MapperKey<K> key, final M mapper) {
		Entries<K> entries;
		Entries<K> newEntries;
		do {
			entries = mapperCache.get();

			final int i = findKey(key, entries);

			if (i >= 0) {
				return;
			}

			int insertionPoint = entries.keys.length;

			newEntries = insertEntry(key, mapper, entries, insertionPoint);

		} while(!mapperCache.compareAndSet(entries, newEntries));
	}

	private Entries<K> insertEntry(MapperKey<K> key, M mapper, Entries<K> entries, int insertionPoint) {
		Entries<K> newEntries;
		newEntries = new Entries<>(entries.keys.length + 1);

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

	private int findKey(MapperKey<K> key, Entries<K> entries) {

		for(int i = 0; i < entries.keys.length; i++) {
			if (key.equals(entries.keys[i])) {
				return i;
			}
		}
		return -1;
	}


	private static class Entries<K extends FieldKey<K>> {
		private MapperKey<K>[] keys;
		private Object[] values;


		Entries(int size) {
			this.keys = new MapperKey[size];
			this.values = new Object[size];
		}
	}

	@Override
	public String toString() {
		return "T2ArraysMapperCache{" +
				"mapperCache=" + Arrays.toString(mapperCache.get().keys) +
				'}';
	}
}
