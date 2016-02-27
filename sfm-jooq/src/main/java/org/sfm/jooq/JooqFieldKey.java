package org.sfm.jooq;

import org.jooq.Field;
import org.sfm.map.FieldKey;

public class JooqFieldKey extends FieldKey<JooqFieldKey> {
	private final Field<?> field;

	public JooqFieldKey(Field<?> field, int index) {
		super(field.getName(), index);
		this.field = field;
	}

	public JooqFieldKey(String alias, Field<?> field, int index, JooqFieldKey parent) {
		super(alias, index, parent);
		this.field = field;
	}

	@Override
	public JooqFieldKey alias(String alias) {
		return new JooqFieldKey(alias, field, index, this);
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
		if (!field.equals(that.field)) return false;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = field.hashCode();
		result = 31 * result + index;
		return result;
	}
}
