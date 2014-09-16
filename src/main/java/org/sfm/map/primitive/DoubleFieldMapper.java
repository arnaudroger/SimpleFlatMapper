package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.DoubleGetter;
import org.sfm.reflect.primitive.DoubleSetter;

public final class DoubleFieldMapper<S, T, K> extends AbstractFieldMapper<S, T, K> {

	private final DoubleGetter<S> getter;
	private final DoubleSetter<T> setter;
	
 	public DoubleFieldMapper(final K key, final DoubleGetter<S> getter, final DoubleSetter<T> setter, final FieldMapperErrorHandler<K> errorHandler) {
 		super(key, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(final S source, final T target) throws Exception {
		setter.setDouble(target, getter.getDouble(source));
	}
}
