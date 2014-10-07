package org.sfm.jdbc.querydsl;

import com.mysema.query.types.Expression;

public class TupleElementKey<T> {
	private final Expression<T> expression;
	private final int index;
	public TupleElementKey(Expression<T> expression, int index) {
		this.expression = expression;
		this.index = index;
	}
	public Expression<T> getExpression() {
		return expression;
	}
	public int getIndex() {
		return index;
	}
}
