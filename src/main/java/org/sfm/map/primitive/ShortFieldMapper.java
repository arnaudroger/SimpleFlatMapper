package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.ShortGetter;
import org.sfm.reflect.primitive.ShortSetter;

public class ShortFieldMapper<S, T> extends AbstractFieldMapper<S, T> {

	private final ShortGetter<S> getter;
	private final ShortSetter<T> setter;
	
 	public ShortFieldMapper(String name, ShortGetter<S> getter, ShortSetter<T> setter, FieldMapperErrorHandler errorHandler) {
 		super(name, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(S source, T target) throws Exception {
		setter.setShort(target, getter.getShort(source));
	}
}
