package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.ByteGetter;
import org.sfm.reflect.primitive.ByteSetter;

public class ByteFieldMapper<S, T> extends AbstractFieldMapper<S, T> {

	private final ByteGetter<S> getter;
	private final ByteSetter<T> setter;
	
 	public ByteFieldMapper(String name, ByteGetter<S> getter, ByteSetter<T> setter, FieldMapperErrorHandler errorHandler) {
 		super(name, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	protected void mapUnsafe(S source, T target) throws Exception {
		setter.setByte(target, getter.getByte(source));
	}
}
