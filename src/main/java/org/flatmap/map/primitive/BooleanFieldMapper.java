package org.flatmap.map.primitive;

import org.flatmap.map.FieldMapper;
import org.flatmap.reflect.primitive.BooleanGetter;
import org.flatmap.reflect.primitive.BooleanSetter;

public class BooleanFieldMapper<S, T> implements FieldMapper<S, T> {

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

}
