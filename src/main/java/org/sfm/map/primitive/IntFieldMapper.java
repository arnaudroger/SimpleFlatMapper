package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.reflect.primitive.IntSetter;

public class IntFieldMapper<S, T> extends AbstractFieldMapper<S, T> {

	private final IntGetter<S> getter;
	private final IntSetter<T> setter;
	
 	public IntFieldMapper(String name, IntGetter<S> getter, IntSetter<T> setter, FieldMapperErrorHandler errorHandler) {
 		super(name, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(S source, T target) throws Exception {
		setter.setInt(target, getter.getInt(source));
	}

}
