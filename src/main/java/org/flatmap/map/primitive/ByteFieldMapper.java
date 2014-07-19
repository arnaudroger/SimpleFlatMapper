package org.flatmap.map.primitive;

import org.flatmap.map.FieldMapper;
import org.flatmap.reflect.primitive.ByteGetter;
import org.flatmap.reflect.primitive.ByteSetter;

public class ByteFieldMapper<S, T> implements FieldMapper<S, T> {

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
