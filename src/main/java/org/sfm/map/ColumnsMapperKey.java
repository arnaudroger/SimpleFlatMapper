package org.sfm.map;

import java.util.Arrays;

public final class ColumnsMapperKey {

	private final String[] columns;
	public ColumnsMapperKey(final String... columns) {
		this.columns = columns;
	}

	@Override
	public boolean equals(final Object obj) {
		final String[] otherColumns = ((ColumnsMapperKey)obj).columns;
        return Arrays.equals(columns, otherColumns);
	}

	public String[] getColumns() {
		return columns;
	}
}
