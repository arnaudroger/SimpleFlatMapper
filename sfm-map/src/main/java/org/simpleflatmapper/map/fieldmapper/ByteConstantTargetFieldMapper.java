package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.setter.ByteContextualSetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;

public final class ByteConstantTargetFieldMapper<S, T> implements FieldMapper<S, T> {

	private final ByteGetter<? super S> getter;
	private final ByteContextualSetter<? super T> setter;
	
	
	public ByteConstantTargetFieldMapper(final ByteGetter<? super S> getter, final ByteContextualSetter<? super T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void mapTo(final S source, final T target, MappingContext<? super S> mappingContext) throws Exception {
        setter.setByte(target, getter.getByte(source), mappingContext);
	}

    @Override
    public String toString() {
        return "ByteFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
