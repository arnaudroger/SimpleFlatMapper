package org.simpleflatmapper.jooq.getter;

import org.jooq.Record;
import org.simpleflatmapper.reflect.Getter;

public class RecordGetter<P> implements Getter<Record, P> {

	private final int index;
	
	public RecordGetter(int index) {
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public P get(Record target) throws Exception {
		return (P) target.getValue(index);
	}

}
