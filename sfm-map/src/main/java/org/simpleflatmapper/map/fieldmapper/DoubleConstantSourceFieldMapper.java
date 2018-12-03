package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.getter.DoubleContextualGetter;
import org.simpleflatmapper.map.setter.ContextualSetterAdapter;
import org.simpleflatmapper.map.setter.DoubleContextualSetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;

public final class DoubleConstantSourceFieldMapper<S, T> implements FieldMapper<S, T> {

	public final DoubleContextualGetter<? super S> getter;
	public final DoubleSetter<? super T> setter;
	
 	public DoubleConstantSourceFieldMapper(final DoubleContextualGetter<? super S> getter, final DoubleSetter<? super T> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
		setter.setDouble(target, getter.getDouble(source, mappingContext));
	}

    @Override
    public String toString() {
        return "DoubleFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
