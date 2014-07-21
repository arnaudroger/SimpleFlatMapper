package org.sfm.map.primitive;

import org.sfm.map.FieldMapper;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.reflect.primitive.IntSetter;

public class IntFieldMapper<S, T> implements FieldMapper<S, T> {

	private final IntGetter<S> getter;
	private final IntSetter<T> setter;
	
	
 	public IntFieldMapper(IntGetter<S> getter, IntSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	public void map(S source, T target) throws Exception {
		setter.setInt(target, getter.getInt(source));
	}

}
