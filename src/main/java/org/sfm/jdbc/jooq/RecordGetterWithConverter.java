package org.sfm.jdbc.jooq;

import org.jooq.Record;
import org.sfm.reflect.Getter;
import org.sfm.utils.Converter;

public class RecordGetterWithConverter<R extends Record, P, F> implements Getter<R, P> {

	private final int index;
	private final Converter<F, P> converter;
	
	public RecordGetterWithConverter(int index, Converter<F, P> converter) {
		this.index = index;
		this.converter = converter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public P get(R target) throws Exception {
		return (P) converter.convert((F) target.getValue(index));
	}

}
