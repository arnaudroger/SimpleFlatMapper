package org.sfm.jdbc.querydsl;

import org.sfm.reflect.EnumHelper;
import org.sfm.reflect.Getter;

import com.mysema.query.Tuple;

public final class EnumTupleOrdinalIndexedGetter<E extends Enum<E>> implements  Getter<Tuple, E> {

	private final int index;
	private final Class<? extends Number> type;
	private final E[] values;
	

	public EnumTupleOrdinalIndexedGetter(TupleElementKey<? extends Number> key, final Class<E> enumType) {
		this.index = key.getIndex();
		this.type = key.getExpression().getType();
		this.values = EnumHelper.getValues(enumType);
	}

	@Override
	public E get(final Tuple target) throws Exception {
		return values[target.get(index, type).intValue()];
	}
}
