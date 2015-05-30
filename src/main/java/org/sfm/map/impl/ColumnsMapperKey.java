package org.sfm.map.impl;

import org.sfm.map.FieldKey;

import java.util.Arrays;

import static org.sfm.utils.Asserts.requireNonNull;

public final class ColumnsMapperKey<K extends FieldKey<K>> {

	private final K[] columns;
	public ColumnsMapperKey(final K... columns) {
		requireNonNull("columns", columns);
		this.columns = columns;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ColumnsMapperKey<?> that = (ColumnsMapperKey<?>) o;

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
        return "ColumnsMapperKey{" + Arrays.toString(columns) +
                '}';
    }
}
