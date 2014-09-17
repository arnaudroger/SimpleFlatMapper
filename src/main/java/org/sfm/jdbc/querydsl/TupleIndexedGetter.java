package org.sfm.jdbc.querydsl;

import org.sfm.reflect.Getter;

import com.mysema.query.Tuple;

public class TupleIndexedGetter<P> implements Getter<Tuple, P> {

	private final Class<? extends P> type;
	private final int index;
	
	public TupleIndexedGetter(Class<? extends P> type, int index) {
		this.type = type;
		this.index = index;
	}

	public TupleIndexedGetter(TupleElementKey<P> key) {
		this(key.getExpression().getType(), key.getIndex());
	}

	@Override
	public P get(Tuple target) throws Exception {
		return target.get(index, type);
	}

}
