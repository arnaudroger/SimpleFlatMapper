package org.sfm.querydsl.getter;

import org.sfm.querydsl.TupleElementKey;
import org.sfm.reflect.EnumHelper;
import org.sfm.reflect.Getter;

import com.mysema.query.Tuple;

public final class EnumTupleOrdinalIndexedGetter<E extends Enum<E>> implements  Getter<Tuple, E> {

	private final int index;
	private final Class<?> type;
	private final E[] values;
	

	public EnumTupleOrdinalIndexedGetter(TupleElementKey key, final Class<E> enumType) {
		this.index = key.getIndex();
		this.type = key.getExpression().getType();
		this.values = EnumHelper.getValues(enumType);
	}

	@Override
	public E get(final Tuple target) throws Exception {
		return values[((Number)target.get(index, type)).intValue()];
	}
}
