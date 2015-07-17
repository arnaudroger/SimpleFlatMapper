package org.sfm.jooq;

import java.util.Arrays;

public final class TargetColumnsMapperKey {

	private final String[] columns;
	private final Class<?> target;
	public TargetColumnsMapperKey(final Class<?> target, final String... columns) {
		this.columns = columns;
		this.target = target;
	}

	@Override
	public boolean equals(final Object obj) {
        TargetColumnsMapperKey targetColumnsMapperKey = (TargetColumnsMapperKey) obj;
        return target == targetColumnsMapperKey.target && Arrays.equals(columns, targetColumnsMapperKey.columns);
    }

	public String[] getColumns() {
		return columns;
	}

	public Class<?> getTarget() {
		return target;
	}
	
	
}
