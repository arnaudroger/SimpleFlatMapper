package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.ShortGetter;
import org.sfm.reflect.primitive.ShortSetter;

public final class ShortFieldMapper<S, T, K> extends AbstractFieldMapper<S, T, K> {

	private final ShortGetter<S> getter;
	private final ShortSetter<T> setter;
	
 	public ShortFieldMapper(final K key, final ShortGetter<S> getter, final ShortSetter<T> setter, final FieldMapperErrorHandler<K> errorHandler) {
 		super(key, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(final S source, final T target) throws Exception {
		setter.setShort(target, getter.getShort(source));
	}
}
