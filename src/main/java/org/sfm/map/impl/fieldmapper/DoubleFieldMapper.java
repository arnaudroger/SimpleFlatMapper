package org.sfm.map.impl.fieldmapper;

import org.sfm.map.impl.FieldMapper;
import org.sfm.reflect.primitive.DoubleGetter;
import org.sfm.reflect.primitive.DoubleSetter;

public final class DoubleFieldMapper<S, T> implements FieldMapper<S, T> {

	private final DoubleGetter<S> getter;
	private final DoubleSetter<T> setter;
	
 	public DoubleFieldMapper(final DoubleGetter<S> getter, final DoubleSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void map(final S source, final T target) throws Exception {
		setter.setDouble(target, getter.getDouble(source));
	}
}
