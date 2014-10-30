package org.sfm.jdbc.jooq;

import org.jooq.Field;
import org.jooq.Record;
import org.sfm.reflect.Getter;

public final class EnumRecordNamedGetter<R extends Record, E extends Enum<E>> implements Getter<R, E> {

	private final Field<?> field;
	private final Class<E> enumType;
	
	public EnumRecordNamedGetter(final JooqFieldKey key, Class<E> enumType) {
		this.field = key.getField();
		this.enumType = enumType;
	}

	@Override
	public E get(final R target) throws Exception {
		final String o = (String) target.getValue(field);
		return (E) Enum.valueOf(enumType, String.valueOf(o));
	}
}
