package org.sfm.map.primitive;

import org.sfm.map.AbstractFieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.primitive.DoubleGetter;
import org.sfm.reflect.primitive.DoubleSetter;

public class DoubleFieldMapper<S, T> extends AbstractFieldMapper<S, T> {

	private final DoubleGetter<S> getter;
	private final DoubleSetter<T> setter;
	
 	public DoubleFieldMapper(String name, DoubleGetter<S> getter, DoubleSetter<T> setter, FieldMapperErrorHandler errorHandler) {
 		super(name, errorHandler);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void mapUnsafe(S source, T target) throws Exception {
		setter.setDouble(target, getter.getDouble(source));
	}
}
