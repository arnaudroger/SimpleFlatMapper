package org.sfm.csv;

import org.sfm.reflect.Setter;

public class DelegateMarkerDelayedCellSetter<T, P> implements DelayedCellSetterFactory<T, P> {

	private final CsvMapper<P> mapper;
	private final Setter<T, P> setter;
	
	public DelegateMarkerDelayedCellSetter(CsvMapper<P> mapper, Setter<T, P> setter) {
		this.mapper = mapper;
		this.setter = setter;
	}

	public DelegateMarkerDelayedCellSetter(CsvMapper<P> mapper) {
		this.mapper = mapper;
		this.setter = null;
	}

	public CsvMapper<P> getMapper() {
		return mapper;
	}

	public Setter<T, P> getSetter() {
		return setter;
	}

	@Override
	public DelayedCellSetter<T, P> newCellSetter() {
		return null;
	}


}
