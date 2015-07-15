package org.sfm.map.impl.fieldmapper;

import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
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
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
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
