package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.BooleanGetter;
import org.sfm.reflect.primitive.BooleanSetter;

public class BooleanFieldMapper<S, T> extends AbstractFieldMapper<S, T> {

	private final BooleanGetter<S> getter;
	private final BooleanSetter<T> setter;
	
	
 	public BooleanFieldMapper(String name, BooleanGetter<S> getter, BooleanSetter<T> setter, FieldMapperErrorHandler errorHandler) {
 		super(name, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	protected void mapUnsafe(S source, T target) throws Exception {
		setter.setBoolean(target, getter.getBoolean(source));
	}

}
