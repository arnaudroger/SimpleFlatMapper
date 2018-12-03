package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ShortContextualGetter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;

public final class ShortConstantSourceFieldMapper<S, T> implements FieldMapper<S, T> {

	public final ShortContextualGetter<? super S> getter;
	public final ShortSetter<? super T> setter;
	
 	public ShortConstantSourceFieldMapper(final ShortContextualGetter<? super S> getter, final ShortSetter<? super T> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
        setter.setShort(target, getter.getShort(source, mappingContext));
	}

    @Override
    public String toString() {
        return "ShortFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
