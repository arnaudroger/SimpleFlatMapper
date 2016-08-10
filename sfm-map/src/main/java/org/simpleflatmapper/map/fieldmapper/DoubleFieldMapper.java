package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;

public final class DoubleFieldMapper<S, T> implements FieldMapper<S, T> {

	private final DoubleGetter<? super S> getter;
	private final DoubleSetter<? super T> setter;
	
 	public DoubleFieldMapper(final DoubleGetter<? super S> getter, final DoubleSetter<? super T> setter) {
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
