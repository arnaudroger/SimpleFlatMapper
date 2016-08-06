package org.simpleflatmapper.jooq.getter;

import org.jooq.Record;
import org.simpleflatmapper.jooq.JooqFieldKey;
import org.simpleflatmapper.reflect.EnumHelper;
import org.simpleflatmapper.reflect.Getter;

public final class EnumRecordOrdinalGetter<R extends Record, E extends Enum<E>> implements Getter<R, E> {

	private final int index;
	private final E[] values;


	public EnumRecordOrdinalGetter(JooqFieldKey key, final Class<E> enumType) {
		this.index = key.getIndex();
		this.values = EnumHelper.getValues(enumType);
	}

	@Override
	public E get(final R target) throws Exception {
		Object value = target.getValue(index);
		if (value == null) {
			return null;
		}
		return values[((Number) value).intValue()];
	}
}
