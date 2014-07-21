package org.sfm.map.primitive;

import org.sfm.map.FieldMapper;
import org.sfm.reflect.primitive.FloatGetter;
import org.sfm.reflect.primitive.FloatSetter;

public class FloatFieldMapper<S, T> implements FieldMapper<S, T> {

	private final FloatGetter<S> getter;
	private final FloatSetter<T> setter;
	
	
 	public FloatFieldMapper(FloatGetter<S> getter, FloatSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	public void map(S source, T target) throws Exception {
		setter.setFloat(target, getter.getFloat(source));
	}

}
