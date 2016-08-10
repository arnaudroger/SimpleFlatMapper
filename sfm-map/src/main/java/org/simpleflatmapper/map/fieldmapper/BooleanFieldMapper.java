package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;

public final class BooleanFieldMapper<S, T> implements FieldMapper<S, T> {

	private final BooleanGetter<? super S> getter;
	private final BooleanSetter<? super T> setter;
	
 	public BooleanFieldMapper(final BooleanGetter<? super S> getter, final BooleanSetter<? super T> setter) {
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
