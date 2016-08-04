package org.simpleflatmapper.core.map.fieldmapper;

import org.simpleflatmapper.core.map.FieldMapper;
import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.core.reflect.primitive.FloatGetter;
import org.simpleflatmapper.core.reflect.primitive.FloatSetter;

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
