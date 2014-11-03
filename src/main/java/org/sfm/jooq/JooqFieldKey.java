package org.sfm.jooq;

import org.jooq.Field;
import org.sfm.map.impl.FieldKey;

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
}
