package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.setter.ShortContextualSetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;

public final class ShortConstantTargetFieldMapper<S, T> implements FieldMapper<S, T> {

	private final ShortGetter<? super S> getter;
	private final ShortContextualSetter<? super T> setter;
	
	public ShortConstantTargetFieldMapper(final ShortGetter<? super S> getter, final ShortContextualSetter<? super T> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
        setter.setShort(target, getter.getShort(source), mappingContext);
	}

    @Override
    public String toString() {
        return "ShortFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
