package org.sfm.jdbc.jooq;

import org.jooq.Field;
import org.jooq.Record;
import org.sfm.reflect.EnumHelper;
import org.sfm.reflect.Getter;

public final class EnumRecordOrdinalGetter<R extends Record, E extends Enum<E>> implements  Getter<R, E> {

	private final Field<?> field;
	private final E[] values;
	

	public EnumRecordOrdinalGetter(JooqFieldKey key, final Class<E> enumType) {
		this.field = key.getField();
		this.values = EnumHelper.getValues(enumType);
	}

	@Override
	public E get(final R target) throws Exception {
		return values[((Number)target.getValue(field)).intValue()];
	}
}
