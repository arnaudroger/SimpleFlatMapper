package org.flatmap.map;

import org.flatmap.reflect.Getter;
import org.flatmap.reflect.Setter;

public class FieldMapper<S, T, P> {
	private final Getter<S, P> getter;
	private final Setter<T, P> setter;
	public FieldMapper(Getter<S, P> getter, Setter<T, P> setter) {
		super();
		this.getter = getter;
		this.setter = setter;
	}
	
	public void map(S source, T target) throws Exception {
		setter.set(target, getter.get(source));
	}
}
