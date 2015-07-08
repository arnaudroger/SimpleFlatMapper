package org.sfm.map.impl.fieldmapper;

import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.primitive.FloatGetter;
import org.sfm.reflect.primitive.FloatSetter;

public final class FloatFieldMapper<S, T> implements FieldMapper<S, T> {

	private final FloatGetter<S> getter;
	private final FloatSetter<T> setter;
	
 	public FloatFieldMapper(final FloatGetter<S> getter, final FloatSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> context) throws Exception {
        setter.setFloat(target, getter.getFloat(source));
	}

    @Override
    public String toString() {
        return "FloatFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
