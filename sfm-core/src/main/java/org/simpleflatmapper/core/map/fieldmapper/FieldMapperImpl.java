package org.simpleflatmapper.core.map.fieldmapper;

import org.simpleflatmapper.core.map.FieldMapper;
import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.Setter;

import static org.simpleflatmapper.core.utils.Asserts.requireNonNull;


public final class FieldMapperImpl<S, T, P> implements FieldMapper<S, T> {
	
	private final Getter<? super S, ? extends P> getter;
	private final Setter<T, ? super P> setter;
	
	public FieldMapperImpl(final Getter<? super S, ? extends P> getter, final Setter<T, ? super P> setter) {
		this.getter = requireNonNull("getter", getter);
		this.setter = requireNonNull("setter", setter);
	}
	
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
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
