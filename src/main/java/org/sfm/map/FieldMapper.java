package org.sfm.map;

import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

public final class FieldMapper<S, T, P> extends AbstractFieldMapper<S, T> {
	
	private final Getter<S, ? extends P> getter;
	private final Setter<T, P> setter;
	
	public FieldMapper(String name, Getter<S, ? extends P> getter, Setter<T, P> setter, FieldMapperErrorHandler errorHandler) {
		super(name, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}
	
	protected final void mapUnsafe(S source, T target) throws Exception {
		final P value = getter.get(source);
		setter.set(target, value);
	}
}
