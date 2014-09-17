package org.sfm.jdbc.querydsl;

import org.sfm.reflect.Getter;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;

public class TupleGetter<P> implements Getter<Tuple, P> {

	private final Expression<P> expr;
	
	public TupleGetter(Expression<P> expr) {
		this.expr = expr;
	}

	@Override
	public P get(Tuple target) throws Exception {
		return target.get(expr);
	}

}
