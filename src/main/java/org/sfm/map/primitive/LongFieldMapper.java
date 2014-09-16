package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.LongGetter;
import org.sfm.reflect.primitive.LongSetter;

public final class LongFieldMapper<S, T, K> extends AbstractFieldMapper<S, T, K> {

	private final LongGetter<S> getter;
	private final LongSetter<T> setter;
	
 	public LongFieldMapper(final K key, final LongGetter<S> getter, final LongSetter<T> setter, final FieldMapperErrorHandler<K> errorHandler) {
 		super(key, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(final S source, final T target) throws Exception {
		setter.setLong(target, getter.getLong(source));
	}
}
