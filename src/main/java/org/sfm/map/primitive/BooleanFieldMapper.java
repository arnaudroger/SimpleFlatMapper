package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.BooleanGetter;
import org.sfm.reflect.primitive.BooleanSetter;

public final class BooleanFieldMapper<S, T, K> extends AbstractFieldMapper<S, T, K> {

	private final BooleanGetter<S> getter;
	private final BooleanSetter<T> setter;
	
 	public BooleanFieldMapper(final K key, final BooleanGetter<S> getter, final BooleanSetter<T> setter, final FieldMapperErrorHandler<K> errorHandler) {
 		super(key, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	protected void mapUnsafe(final S source, final T target) throws Exception {
		setter.setBoolean(target, getter.getBoolean(source));
	}

}
