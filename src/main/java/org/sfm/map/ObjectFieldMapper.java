package org.sfm.map;

import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

public class ObjectFieldMapper<S, T, P> implements FieldMapper<S, T> {
	private final Getter<S, ? extends P> getter;
	private final Setter<T, P> setter;
	public ObjectFieldMapper(Getter<S, ? extends P> getter, Setter<T, P> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public void map(S source, T target) throws Exception {
		setter.set(target, getter.get(source));
	}
}
