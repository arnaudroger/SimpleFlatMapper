package org.sfm.map;

import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

public final class FieldMapperImpl<S, T, P, K> extends AbstractFieldMapper<S, T, K> {
	
	private final Getter<S, ? extends P> getter;
	private final Setter<T, P> setter;
	
	public FieldMapperImpl(final K key, final Getter<S, ? extends P> getter, final Setter<T, P> setter, final FieldMapperErrorHandler<K> errorHandler) {
		super(key, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}
	
	protected final void mapUnsafe(final S source, final T target) throws Exception {
		final P value = getter.get(source);
		setter.set(target, value);
	}
}
