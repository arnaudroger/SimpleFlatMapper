package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.FloatGetter;
import org.sfm.reflect.primitive.FloatSetter;

public class FloatFieldMapper<S, T> extends AbstractFieldMapper<S, T> {

	private final FloatGetter<S> getter;
	private final FloatSetter<T> setter;
	
 	public FloatFieldMapper(String name, FloatGetter<S> getter, FloatSetter<T> setter, FieldMapperErrorHandler errorHandler) {
 		super(name, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(S source, T target) throws Exception {
		setter.setFloat(target, getter.getFloat(source));
	}
}
