package org.sfm.jooq.getter;

import org.jooq.Record;
import org.sfm.jooq.JooqFieldKey;
import org.sfm.reflect.Getter;

public final class EnumRecordNamedGetter<R extends Record, E extends Enum<E>> implements Getter<R, E> {

	private final int index;
	private final Class<E> enumType;

	public EnumRecordNamedGetter(final JooqFieldKey key, Class<E> enumType) {
		this.index = key.getIndex();
		this.enumType = enumType;
	}

	@Override
	public E get(final R target) throws Exception {
		final Object o = target.getValue(index);
		if (o == null) {
			return null;
		}
		return (E) Enum.valueOf(enumType, String.valueOf(o));
	}
}
