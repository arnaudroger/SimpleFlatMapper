package org.simpleflatmapper.core.map.fieldmapper;

import org.simpleflatmapper.core.map.FieldMapper;
import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.core.reflect.primitive.IntGetter;
import org.simpleflatmapper.core.reflect.primitive.IntSetter;

public final class IntFieldMapper<S, T> implements FieldMapper<S, T> {

	private final IntGetter<S> getter;
	private final IntSetter<T> setter;
	
 	public IntFieldMapper(final IntGetter<S> getter, final IntSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
        setter.setInt(target, getter.getInt(source));
	}

    @Override
    public String toString() {
        return "IntFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
