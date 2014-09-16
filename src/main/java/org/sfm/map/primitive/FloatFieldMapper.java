package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.FloatGetter;
import org.sfm.reflect.primitive.FloatSetter;

public final class FloatFieldMapper<S, T, K> extends AbstractFieldMapper<S, T, K> {

	private final FloatGetter<S> getter;
	private final FloatSetter<T> setter;
	
 	public FloatFieldMapper(final K key, final FloatGetter<S> getter, final FloatSetter<T> setter, final FieldMapperErrorHandler<K> errorHandler) {
 		super(key, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(final S source, final T target) throws Exception {
		setter.setFloat(target, getter.getFloat(source));
	}
}
