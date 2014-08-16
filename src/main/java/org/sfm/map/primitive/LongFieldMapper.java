package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.LongGetter;
import org.sfm.reflect.primitive.LongSetter;

public class LongFieldMapper<S, T> extends AbstractFieldMapper<S, T> {

	private final LongGetter<S> getter;
	private final LongSetter<T> setter;
	
 	public LongFieldMapper(String name, LongGetter<S> getter, LongSetter<T> setter, FieldMapperErrorHandler errorHandler) {
 		super(name, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(S source, T target) throws Exception {
		setter.setLong(target, getter.getLong(source));
	}
}
