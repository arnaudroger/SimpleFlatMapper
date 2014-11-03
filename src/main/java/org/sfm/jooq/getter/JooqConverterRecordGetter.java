package org.sfm.jooq.getter;

import org.jooq.Record;
import org.sfm.reflect.Getter;

public class JooqConverterRecordGetter<R extends Record, P> implements Getter<R, P> {

	private final int index;
	private final Class<P> propertyClass;
	
	public JooqConverterRecordGetter(int index, Class<P> propertyClass) {
		this.index = index;
		this.propertyClass = propertyClass;
	}

	@Override
	public P get(R target) throws Exception {
		return target.getValue(index, propertyClass);
	}

}
