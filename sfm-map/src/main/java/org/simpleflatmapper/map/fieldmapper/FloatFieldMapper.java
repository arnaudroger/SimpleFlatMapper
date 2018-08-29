package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.getter.FloatContextualGetter;
import org.simpleflatmapper.map.setter.ContextualSetterAdapter;
import org.simpleflatmapper.map.setter.FloatContextualSetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;

public final class FloatFieldMapper<S, T> implements FieldMapper<S, T> {

	private final FloatContextualGetter<? super S> getter;
	private final FloatContextualSetter<? super T> setter;
	
 	public FloatFieldMapper(final FloatContextualGetter<? super S> getter, final FloatSetter<? super T> setter) {
		this.getter = getter;
		this.setter = ContextualSetterAdapter.of(setter);
	}
	public FloatFieldMapper(final FloatGetter<? super S> getter, final FloatContextualSetter<? super T> setter) {
		this.getter = ContextualGetterAdapter.of(getter);
		this.setter = setter;
	}
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> context) throws Exception {
        setter.setFloat(target, getter.getFloat(source, context), context);
	}

    @Override
    public String toString() {
        return "FloatFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
