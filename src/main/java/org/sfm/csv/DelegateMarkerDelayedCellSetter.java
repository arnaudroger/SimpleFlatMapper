package org.sfm.csv;

import java.lang.reflect.Type;

import org.sfm.reflect.Setter;

public class DelegateMarkerDelayedCellSetter<T, P> implements DelayedCellSetter<T, P> {

	private final CsvMapper<P> mapper;
	private final Setter<T, P> setter;
	private final Type type;
	
	public DelegateMarkerDelayedCellSetter(CsvMapper<P> mapper, Setter<T, P> setter) {
		this.mapper = mapper;
		this.setter = setter;
		this.type = setter.getPropertyType();
	}

	public DelegateMarkerDelayedCellSetter(CsvMapper<P> mapper, Type type) {
		this.mapper = mapper;
		this.setter = null;
		this.type = type;
	}

	public CsvMapper<P> getMapper() {
		return mapper;
	}

	public Setter<T, P> getSetter() {
		return setter;
	}

	public Type getType() {
		return type;
	}
	
	public boolean hasSetter() {
		return setter != null;
	}

	@Override
	public DelayedSetter<T, P> set(byte[] bytes, int offset, int length)
			throws Exception {
		throw new UnsupportedOperationException();
	}

}
