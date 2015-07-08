package org.sfm.map.impl.fieldmapper;

import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.reflect.primitive.IntSetter;

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
