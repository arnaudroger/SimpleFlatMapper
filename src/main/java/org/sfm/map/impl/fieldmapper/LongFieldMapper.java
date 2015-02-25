package org.sfm.map.impl.fieldmapper;

import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.primitive.LongGetter;
import org.sfm.reflect.primitive.LongSetter;

public final class LongFieldMapper<S, T> implements FieldMapper<S, T> {

	private final LongGetter<S> getter;
	private final LongSetter<T> setter;
	
 	public LongFieldMapper(final LongGetter<S> getter, final LongSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void mapTo(final S source, final T target, final MappingContext<S> mappingContext) throws Exception {
		setter.setLong(target, getter.getLong(source));
	}

    @Override
    public String toString() {
        return "LongFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
