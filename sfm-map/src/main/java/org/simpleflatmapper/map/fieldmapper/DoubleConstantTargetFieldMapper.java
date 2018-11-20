package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.setter.DoubleContextualSetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;

public final class DoubleConstantTargetFieldMapper<S, T> implements FieldMapper<S, T> {

	private final DoubleGetter<? super S> getter;
	private final DoubleContextualSetter<? super T> setter;
	

	public DoubleConstantTargetFieldMapper(final DoubleGetter<? super S> getter, final DoubleContextualSetter<? super T> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
		setter.setDouble(target, getter.getDouble(source), mappingContext);
	}

    @Override
    public String toString() {
        return "DoubleFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
