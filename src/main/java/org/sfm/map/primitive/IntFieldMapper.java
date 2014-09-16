package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.reflect.primitive.IntSetter;

public final class IntFieldMapper<S, T, K> extends AbstractFieldMapper<S, T, K> {

	private final IntGetter<S> getter;
	private final IntSetter<T> setter;
	
 	public IntFieldMapper(final K key, final IntGetter<S> getter, final IntSetter<T> setter, final FieldMapperErrorHandler<K> errorHandler) {
 		super(key, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(final S source, final T target) throws Exception {
		setter.setInt(target, getter.getInt(source));
	}

}
