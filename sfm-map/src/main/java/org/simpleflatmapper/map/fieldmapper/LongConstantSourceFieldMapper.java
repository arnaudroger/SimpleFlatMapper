package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.LongContextualGetter;
import org.simpleflatmapper.reflect.primitive.LongSetter;

public final class LongConstantSourceFieldMapper<S, T> implements FieldMapper<S, T> {

	public final LongContextualGetter<? super S> getter;
	public final LongSetter<? super T> setter;
	
 	public LongConstantSourceFieldMapper(final LongContextualGetter<? super S> getter, final LongSetter<? super T> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
		setter.setLong(target, getter.getLong(source, mappingContext));
	}

    @Override
    public String toString() {
        return "LongFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
