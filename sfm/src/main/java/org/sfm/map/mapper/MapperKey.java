package org.sfm.map.mapper;

import org.sfm.map.FieldKey;

import java.util.Arrays;

import static org.sfm.utils.Asserts.requireNonNull;

public final class MapperKey<K extends FieldKey<K>> {

	private final K[] columns;

	@SuppressWarnings("unchecked")
	public MapperKey(final K... columns) {
		requireNonNull("columns", columns);
		this.columns = columns;
	}

	@Override
	public boolean equals(Object o) {
		MapperKey<?> that = (MapperKey<?>) o;
		return Arrays.equals(columns, that.columns);
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
