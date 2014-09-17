package org.sfm.jdbc.querydsl;

import org.sfm.reflect.EnumHelper;
import org.sfm.reflect.Getter;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;

public final class EnumTupleOrdinalGetter<E extends Enum<E>> implements  Getter<Tuple, E> {

	private final Expression<? extends Number> expr;
	private final E[] values;
	
	public EnumTupleOrdinalGetter(final Expression<? extends Number> expr, final Class<E> enumType) {
		this.expr = expr;
		this.values = EnumHelper.getValues(enumType);
	}

	@Override
	public E get(final Tuple target) throws Exception {
		return values[target.get(expr).intValue()];
	}
}
