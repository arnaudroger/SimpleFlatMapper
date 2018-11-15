package org.simpleflatmapper.map;

import org.simpleflatmapper.map.mapper.MapperKey;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public final class T2ArraysMapperCache<K extends FieldKey<K>, M> implements IMapperCache<K, M> {

	@SuppressWarnings("unchecked")
	private final AtomicReference<Entries<K>> sortedEntries = new AtomicReference<Entries<K>>(new Entries<K>(0));

	@SuppressWarnings("unchecked")
	public void add(final MapperKey<K> key, final M mapper) {
		Entries<K> entries;
		Entries<K> newEntries;
		do {
			entries = this.sortedEntries.get();

			final int i = entries.findKey(key);

			if (i >= 0) {
				if (!key.equals(entries.keys[i])) {
					throw new IllegalStateException("Comparator find key " + key + " to be equal to " + entries.keys[i] + " but is not");
				}
				return;
			}

			int insertionPoint = -1 - i;

			newEntries = entries.insertEntry(key, mapper, insertionPoint);

		} while(!this.sortedEntries.compareAndSet(entries, newEntries));
	}


	@SuppressWarnings("unchecked")
	public M get(MapperKey<K> key) {
		return (M) sortedEntries.get().search(key);
	}

	@Override
	public int size() {
		return sortedEntries.get().keys.length;
	}

	private static class Entries<K extends FieldKey<K>> {
		private final MapperKey<K>[] keys;
		private final Object[] values;

		@SuppressWarnings("unchecked")
		private Entries(int size) {
			this.keys = new MapperKey[size];
			this.values = new Object[size];
		}

		private Object search(MapperKey<K> key) {
			final int i = findKey(key);
			if (i >= 0) {
				return values[i];
			}
			return null;
		}

		private int findKey(MapperKey<K> key) {
			for(int i = 0; i < keys.length; i++) {
				if (key.equals(keys[i])) {
					return i;
				}
			}
			return - keys.length - 1;
		}



		private Entries<K> insertEntry(MapperKey<K> key, Object mapper, int insertionPoint) {
			Entries<K> newEntries = new Entries<K>(keys.length + 1);

			System.arraycopy(keys, 0, newEntries.keys, 0, insertionPoint);
			System.arraycopy(values, 0, newEntries.values, 0, insertionPoint);

			newEntries.keys[insertionPoint] = key;
			newEntries.values[insertionPoint] = mapper;

			System.arraycopy(keys, insertionPoint, newEntries.keys, insertionPoint + 1, keys.length - insertionPoint);
			System.arraycopy(values, insertionPoint, newEntries.values, insertionPoint + 1, keys.length - insertionPoint);
			return newEntries;

		}
	}

	@Override
	public String toString() {
		return "Sorted2ArraysMapperCache{" +
				"sortedEntries=" + Arrays.toString(sortedEntries.get().keys) +
				'}';
	}
}
