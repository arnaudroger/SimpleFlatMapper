package org.sfm.map.primitive;

import org.sfm.map.FieldMapper;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.reflect.primitive.IntSetter;

public final class IntFieldMapper<S, T> implements FieldMapper<S, T> {

	private final IntGetter<S> getter;
	private final IntSetter<T> setter;
	
 	public IntFieldMapper(final IntGetter<S> getter, final IntSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void map(final S source, final T target) throws Exception {
		setter.setInt(target, getter.getInt(source));
	}

}
