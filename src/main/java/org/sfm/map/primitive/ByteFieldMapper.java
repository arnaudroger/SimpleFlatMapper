package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.ByteGetter;
import org.sfm.reflect.primitive.ByteSetter;

public final class ByteFieldMapper<S, T> extends AbstractFieldMapper<S, T> {

	private final ByteGetter<S> getter;
	private final ByteSetter<T> setter;
	
 	public ByteFieldMapper(final String name, final ByteGetter<S> getter, final ByteSetter<T> setter, final FieldMapperErrorHandler errorHandler) {
 		super(name, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	protected void mapUnsafe(final S source, final T target) throws Exception {
		setter.setByte(target, getter.getByte(source));
	}
}
