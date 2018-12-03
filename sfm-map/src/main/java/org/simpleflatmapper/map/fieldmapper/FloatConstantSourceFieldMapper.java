package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.FloatContextualGetter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;

public final class FloatConstantSourceFieldMapper<S, T> implements FieldMapper<S, T> {

	public final FloatContextualGetter<? super S> getter;
	public final FloatSetter<? super T> setter;
	
 	public FloatConstantSourceFieldMapper(final FloatContextualGetter<? super S> getter, final FloatSetter<? super T> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> context) throws Exception {
        setter.setFloat(target, getter.getFloat(source, context));
	}

    @Override
    public String toString() {
        return "FloatFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
