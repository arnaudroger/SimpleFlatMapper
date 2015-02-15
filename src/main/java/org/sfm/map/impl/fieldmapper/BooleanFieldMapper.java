package org.sfm.map.impl.fieldmapper;

import org.sfm.map.impl.FieldMapper;
import org.sfm.reflect.primitive.BooleanGetter;
import org.sfm.reflect.primitive.BooleanSetter;

public final class BooleanFieldMapper<S, T> implements FieldMapper<S, T> {

	private final BooleanGetter<S> getter;
	private final BooleanSetter<T> setter;
	
 	public BooleanFieldMapper(final BooleanGetter<S> getter, final BooleanSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	public void map(final S source, final T target) throws Exception {
		setter.setBoolean(target, getter.getBoolean(source));
	}

    @Override
    public String toString() {
        return "BooleanFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
