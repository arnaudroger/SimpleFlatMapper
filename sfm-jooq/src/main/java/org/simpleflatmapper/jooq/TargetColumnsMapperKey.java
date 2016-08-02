package org.simpleflatmapper.jooq;

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

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(columns);
		result = 31 * result + target.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "TargetColumnsMapperKey{" +
				"columns=" + Arrays.toString(columns) +
				", target=" + target +
				'}';
	}
}
