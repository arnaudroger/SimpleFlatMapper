package org.sfm.jooq;

import org.jooq.Field;
import org.sfm.map.FieldKey;

public class JooqFieldKey implements FieldKey<JooqFieldKey> {
	private final Field<?> field;
	private final int index;

	public JooqFieldKey(Field<?> field, int index) {
		this.field = field;
		this.index = index;
	}

	@Override
	public String getName() {
		return field.getName();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public JooqFieldKey alias(String alias) {
		throw new UnsupportedOperationException();
	}

	public Field<?> getField() {
		return field;
	}

	@Override
	public String toString() {
		return "JooqFieldKey{" +
				"field=" + field +
				", index=" + index +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		JooqFieldKey that = (JooqFieldKey) o;

		if (index != that.index) return false;
		return field.equals(that.field);

	}

	@Override
	public int hashCode() {
		int result = field.hashCode();
		result = 31 * result + index;
		return result;
	}
}
