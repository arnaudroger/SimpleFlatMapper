package org.sfm.map.primitive;

import org.sfm.map.FieldMapper;
import org.sfm.reflect.primitive.ShortGetter;
import org.sfm.reflect.primitive.ShortSetter;

public final class ShortFieldMapper<S, T> implements FieldMapper<S, T> {

	private final ShortGetter<S> getter;
	private final ShortSetter<T> setter;
	
 	public ShortFieldMapper(final ShortGetter<S> getter, final ShortSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void map(final S source, final T target) throws Exception {
		setter.setShort(target, getter.getShort(source));
	}
}
