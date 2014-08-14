package org.sfm.map.primitive;

import org.sfm.map.Mapper;
import org.sfm.reflect.primitive.FloatGetter;
import org.sfm.reflect.primitive.FloatSetter;

public class FloatFieldMapper<S, T> implements Mapper<S, T> {

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


	public FloatGetter<S> getGetter() {
		return getter;
	}


	public FloatSetter<T> getSetter() {
		return setter;
	}

}
