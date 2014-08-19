package org.sfm.map;

import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

public final class FieldMapper<S, T, P> extends AbstractFieldMapper<S, T> {
	
	private final Getter<S, ? extends P> getter;
	private final Setter<T, P> setter;
	
	public FieldMapper(final String name, final Getter<S, ? extends P> getter, final Setter<T, P> setter, final FieldMapperErrorHandler errorHandler) {
		super(name, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}
	
	protected final void mapUnsafe(final S source, final T target) throws Exception {
		final P value = getter.get(source);
		setter.set(target, value);
	}
}
