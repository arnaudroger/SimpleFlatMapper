package org.sfm.jdbc.jooq;

import org.jooq.Record;
import org.sfm.reflect.Getter;

public class RecordGetter<R extends Record, P> implements Getter<R, P> {

	private final int index;
	private final Class<P> type;
	
	public RecordGetter(int index, Class<P> type) {
		this.index = index;
		this.type = type;
	}

	@Override
	public P get(R target) throws Exception {
		return target.getValue(index, type);
	}

}
