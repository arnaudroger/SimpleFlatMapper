package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;

public final class ByteFieldMapper<S, T> implements FieldMapper<S, T> {

	private final ByteGetter<S> getter;
	private final ByteSetter<T> setter;
	
 	public ByteFieldMapper(final ByteGetter<S> getter, final ByteSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	public void mapTo(final S source, final T target, MappingContext<? super S> mappingContext) throws Exception {
        setter.setByte(target, getter.getByte(source));
	}

    @Override
    public String toString() {
        return "ByteFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
