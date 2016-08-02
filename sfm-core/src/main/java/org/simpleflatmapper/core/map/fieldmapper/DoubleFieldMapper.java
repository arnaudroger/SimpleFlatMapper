package org.simpleflatmapper.core.map.fieldmapper;

import org.simpleflatmapper.core.map.FieldMapper;
import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.core.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.core.reflect.primitive.DoubleSetter;

public final class DoubleFieldMapper<S, T> implements FieldMapper<S, T> {

	private final DoubleGetter<S> getter;
	private final DoubleSetter<T> setter;
	
 	public DoubleFieldMapper(final DoubleGetter<S> getter, final DoubleSetter<T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
		setter.setDouble(target, getter.getDouble(source));
	}

    @Override
    public String toString() {
        return "DoubleFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
