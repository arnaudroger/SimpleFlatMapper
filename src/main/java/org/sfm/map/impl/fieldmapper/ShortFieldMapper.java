package org.sfm.map.impl.fieldmapper;

import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.primitive.ShortGetter;
import org.sfm.reflect.primitive.ShortSetter;

public final class ShortFieldMapper<S, T> implements FieldMapper<S, T> {

	private final ShortGetter<S> getter;
	private final ShortSetter<T> setter;
	
 	public ShortFieldMapper(final ShortGetter<S> getter, final ShortSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void mapTo(final S source, final T target, final MappingContext mappingContext) throws Exception {
        setter.setShort(target, getter.getShort(source));
	}

    @Override
    public String toString() {
        return "ShortFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
