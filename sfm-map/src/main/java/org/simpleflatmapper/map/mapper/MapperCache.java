package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public final class MapperCache<K extends FieldKey<K>, M> {

	private static final int SIZE_THRESHOLD = 60;
	@SuppressWarnings("unchecked")
	private final AtomicReference<SortedEntries<K>> sortedEntries;

	public MapperCache(MapperKeyComparator<K> comparator) {
		this.sortedEntries =
				new AtomicReference<SortedEntries<K>>(
						new SortedEntries<K>(0, comparator));
	}

	@SuppressWarnings("unchecked")
	public void add(final MapperKey<K> key, final M mapper) {
		SortedEntries<K> sortedEntries;
		SortedEntries<K> newSortedEntries;
		do {
			sortedEntries = this.sortedEntries.get();

			final int i = sortedEntries.findInsertionPoint(key);

			if (i >= 0) {
				if (!key.equals(sortedEntries.keys[i])) {
					throw new IllegalStateException("Comparator find key " + key + " to be equal to " + sortedEntries.keys[i] + " but is not");
				}
				return;
			}

			int insertionPoint = -1 - i;

			newSortedEntries = sortedEntries.insertEntry(key, mapper, insertionPoint);

		} while(!this.sortedEntries.compareAndSet(sortedEntries, newSortedEntries));
	}


	@SuppressWarnings("unchecked")
	public M get(MapperKey<K> key) {
		return (M) sortedEntries.get().search(key);
	}

	private static final class SortedEntries<K extends FieldKey<K>> {
		private final MapperKey<K>[] keys;
		private final Object[] values;
		private final boolean bsearch;
		private final MapperKeyComparator<K> comparator;

		@SuppressWarnings("unchecked")
		SortedEntries(int size, MapperKeyComparator<K> comparator) {
			this.comparator = comparator;
			this.keys = new MapperKey[size];
			this.values = new Object[size];
			this.bsearch =  size > SIZE_THRESHOLD;
		}

		SortedEntries(MapperKey<K>[] keys, Object[] values, MapperKeyComparator<K> comparator) {
			this.keys = keys;
			this.values = values;
			this.comparator = comparator;
			this.bsearch = keys.length > SIZE_THRESHOLD;
		}

		Object search(MapperKey<K> key) {
			final int i = findKey(key);
			if (i >= 0) {
				return values[i];
			}
			return null;
		}

		int findKey(MapperKey<K> key) {
			if (bsearch) {
				return Arrays.binarySearch(keys, key, comparator);
			} else {
				return iFindKey(key);
			}
		}

		private int iFindKey(MapperKey<K> key) {
			for(int i = 0; i < keys.length; i++) {
				if (key.equals(keys[i])) {
					return i;
				}
			}
			return - keys.length - 1;
		}

		private int findInsertionPoint(MapperKey<K> key) {
			if (comparator == null) {
				return iFindKey(key);
			} else {
				return Arrays.binarySearch(keys, key, comparator);
			}
		}

		SortedEntries<K> insertEntry(MapperKey<K> key, Object mapper, int insertionPoint) {

			SortedEntries<K> newEntries =
					new SortedEntries<K>(
						Arrays.copyOf(keys, keys.length + 1),
						Arrays.copyOf(values, values.length + 1),
					 	comparator);

			System.arraycopy(newEntries.keys, insertionPoint, newEntries.keys, insertionPoint + 1, keys.length - insertionPoint);
			System.arraycopy(newEntries.values, insertionPoint, newEntries.values, insertionPoint + 1, values.length - insertionPoint);

			newEntries.keys[insertionPoint] = key;
			newEntries.values[insertionPoint] = mapper;

			return newEntries;

		}
	}

	@Override
	public String toString() {
		return "MapperCache{" +
				"sortedEntries=" + Arrays.toString(sortedEntries.get().keys) +
				'}';
	}
}
