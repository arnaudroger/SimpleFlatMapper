package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;

import java.util.Arrays;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class MapperKey<K extends FieldKey<K>> {

	private final K[] columns;

	@SuppressWarnings("unchecked")
	public MapperKey(final K... columns) {
		requireNonNull("columns", columns);
		this.columns = columns;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapperKey<?> that = (MapperKey<?>) obj;
		return equals(columns, that.columns);
	}

	private boolean equals(K[] c1, FieldKey<?>[] c2) {
		if (c1.length != c2.length)
			return false;
		for(int i = 0; i < c1.length; i++) {
			if (!c1[i].equals(c2[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(columns);
	}

	public K[] getColumns() {
		return columns;
	}

    @Override
    public String toString() {
        return "MapperKey{" + Arrays.toString(columns) +
                '}';
    }
}
