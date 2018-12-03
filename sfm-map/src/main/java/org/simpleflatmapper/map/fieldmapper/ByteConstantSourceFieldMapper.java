package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ByteContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.setter.ByteContextualSetter;
import org.simpleflatmapper.map.setter.ContextualSetterAdapter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;

public final class ByteConstantSourceFieldMapper<S, T> implements FieldMapper<S, T> {

	public final ByteContextualGetter<? super S> getter;
	public final ByteSetter<? super T> setter;
	
 	public ByteConstantSourceFieldMapper(final ByteContextualGetter<? super S> getter, final ByteSetter<? super T> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public void mapTo(final S source, final T target, MappingContext<? super S> mappingContext) throws Exception {
        setter.setByte(target, getter.getByte(source, mappingContext));
	}

    @Override
    public String toString() {
        return "ByteFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
