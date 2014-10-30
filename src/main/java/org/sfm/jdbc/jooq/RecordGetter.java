package org.sfm.jdbc.jooq;

import org.jooq.Field;
import org.jooq.Record;
import org.sfm.reflect.Getter;

public class RecordGetter<R extends Record, P> implements Getter<R, P> {

	private final Field<P> field;
	
	public RecordGetter(Field<P> field) {
		this.field = field;
	}

	@Override
	public P get(R target) throws Exception {
		return target.getValue(field);
	}

}
