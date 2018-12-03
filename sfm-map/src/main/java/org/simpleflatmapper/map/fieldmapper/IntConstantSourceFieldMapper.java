package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.IntContextualGetter;
import org.simpleflatmapper.reflect.primitive.IntSetter;

import static java.util.Objects.requireNonNull;

public final class IntConstantSourceFieldMapper<S, T> implements FieldMapper<S, T> {

	public final IntContextualGetter<? super S> getter;
	public final IntSetter<? super T> setter;
	
 	public IntConstantSourceFieldMapper(final IntContextualGetter<? super S> getter, final IntSetter<? super T> setter) {
		this.getter = requireNonNull(getter);
		this.setter = requireNonNull(setter);
	}
	

	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
        setter.setInt(target, getter.getInt(source, mappingContext));
	}

    @Override
    public String toString() {
        return "IntFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
