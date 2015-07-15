package org.sfm.querydsl.getter;

import com.mysema.query.Tuple;
import org.sfm.querydsl.TupleElementKey;
import org.sfm.reflect.Getter;

public class TupleIndexedGetter<P> implements Getter<Tuple, P> {

	private final Class<? extends P> type;
	private final int index;
	
	public TupleIndexedGetter(Class<? extends P> type, int index) {
		this.type = type;
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	public TupleIndexedGetter(TupleElementKey key) {
		this((Class<? extends P>) key.getExpression().getType(), key.getIndex());
	}

	@Override
	public P get(Tuple target) throws Exception {
		return target.get(index, type);
	}

}
