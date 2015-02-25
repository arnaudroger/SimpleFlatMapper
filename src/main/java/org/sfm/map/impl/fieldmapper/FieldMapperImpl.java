package org.sfm.map.impl.fieldmapper;

import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

public final class FieldMapperImpl<S, T, P> implements FieldMapper<S, T> {
	
	private final Getter<S, ? extends P> getter;
	private final Setter<T, ? super P> setter;
	
	public FieldMapperImpl(final Getter<S, ? extends P> getter, final Setter<T, ? super P> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public void mapTo(final S source, final T target, final MappingContext<S> mappingContext) throws Exception {
		final P value = getter.get(source);
		setter.set(target, value);
	}

    @Override
    public String toString() {
        return "FieldMapperImpl{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
