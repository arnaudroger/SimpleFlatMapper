package org.sfm.map.primitive;

import org.sfm.map.Mapper;
import org.sfm.reflect.primitive.ByteGetter;
import org.sfm.reflect.primitive.ByteSetter;

public class ByteFieldMapper<S, T> implements Mapper<S, T> {

	private final ByteGetter<S> getter;
	private final ByteSetter<T> setter;
	
	
 	public ByteFieldMapper(ByteGetter<S> getter, ByteSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	public void map(S source, T target) throws Exception {
		setter.setByte(target, getter.getByte(source));
	}

}
