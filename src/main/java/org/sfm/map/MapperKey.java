package org.sfm.map;

import java.util.Arrays;

public final class MapperKey {

	private final String[] columns;

	public MapperKey(final String... columns) {
		this.columns = columns;
	}

	@Override
	public boolean equals(final Object obj) {
		final String[] otherColumns = ((MapperKey)obj).columns;
        return Arrays.equals(columns, otherColumns);
	}

	public String[] getColumns() {
		return columns;
	}
}
