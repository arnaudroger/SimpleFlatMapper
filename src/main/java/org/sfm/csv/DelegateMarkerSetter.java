package org.sfm.csv;

import org.sfm.reflect.Setter;

public class DelegateMarkerSetter<T> implements CellSetter<T> {

	private final CsvMapper<?> mapper;
	private final Setter<T, ?> setter;
 
	public DelegateMarkerSetter(CsvMapper<?> mapper, Setter<T, ?> setter) {
		this.mapper = mapper;
		this.setter = setter;
	}

	@Override
	public void set(T target, byte[] bytes, int offset, int length)
			throws Exception {
		throw new UnsupportedOperationException();
	}

	public CsvMapper<?> getMapper() {
		return mapper;
	}


	public Setter<T, ?> getSetter() {
		return setter;
	}
	
	
}
