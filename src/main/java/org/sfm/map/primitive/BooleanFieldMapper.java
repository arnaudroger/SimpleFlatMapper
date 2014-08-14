package org.sfm.map.primitive;

import org.sfm.map.Mapper;
import org.sfm.reflect.primitive.BooleanGetter;
import org.sfm.reflect.primitive.BooleanSetter;

public class BooleanFieldMapper<S, T> implements Mapper<S, T> {

	private final BooleanGetter<S> getter;
	private final BooleanSetter<T> setter;
	
	
 	public BooleanFieldMapper(BooleanGetter<S> getter, BooleanSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	public void map(S source, T target) throws Exception {
		setter.setBoolean(target, getter.getBoolean(source));
	}


	public BooleanGetter<S> getGetter() {
		return getter;
	}


	public BooleanSetter<T> getSetter() {
		return setter;
	}

}
