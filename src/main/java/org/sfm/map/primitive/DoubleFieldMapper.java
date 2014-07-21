package org.sfm.map.primitive;

import org.sfm.map.FieldMapper;
import org.sfm.reflect.primitive.DoubleGetter;
import org.sfm.reflect.primitive.DoubleSetter;

public class DoubleFieldMapper<S, T> implements FieldMapper<S, T> {

	private final DoubleGetter<S> getter;
	private final DoubleSetter<T> setter;
	
	
 	public DoubleFieldMapper(DoubleGetter<S> getter, DoubleSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	public void map(S source, T target) throws Exception {
		setter.setDouble(target, getter.getDouble(source));
	}

}
