package org.simpleflatmapper.jooq.getter;

import org.jooq.Record;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.converter.Converter;

public class RecordGetterWithConverter<R extends Record, P, F> implements Getter<R, P> {

	private final int index;
	private final Converter<? super F, ? extends P> converter;
	
	public RecordGetterWithConverter(int index, Converter<? super F, ? extends P> converter) {
		this.index = index;
		this.converter = converter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public P get(R target) throws Exception {
		F value = (F) target.getValue(index);
		if (value != null) {
			return (P) converter.convert(value);
		} else {
			return null;
		}
	}
	
	
}
