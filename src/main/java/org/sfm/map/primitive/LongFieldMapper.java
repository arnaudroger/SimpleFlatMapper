package org.sfm.map.primitive;

import org.sfm.map.Mapper;
import org.sfm.reflect.primitive.LongGetter;
import org.sfm.reflect.primitive.LongSetter;

public class LongFieldMapper<S, T> implements Mapper<S, T> {

	private final LongGetter<S> getter;
	private final LongSetter<T> setter;
	
	
 	public LongFieldMapper(LongGetter<S> getter, LongSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	public void map(S source, T target) throws Exception {
		setter.setLong(target, getter.getLong(source));
	}

}
