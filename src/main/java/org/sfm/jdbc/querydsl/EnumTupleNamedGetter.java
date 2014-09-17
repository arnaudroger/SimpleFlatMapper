package org.sfm.jdbc.querydsl;

import org.sfm.reflect.Getter;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;

public final class EnumTupleNamedGetter<E extends Enum<E>> implements Getter<Tuple, E> {

	private final Expression<String> expr;
	private final Class<E> enumType;
	
	public EnumTupleNamedGetter(final Expression<String> expr, final Class<E> enumType) {
		this.expr = expr;
		this.enumType = enumType;
	}

	@Override
	public E get(final Tuple target) throws Exception {
		final String o = target.get(expr);
		return (E) Enum.valueOf(enumType, String.valueOf(o));
	}
}
