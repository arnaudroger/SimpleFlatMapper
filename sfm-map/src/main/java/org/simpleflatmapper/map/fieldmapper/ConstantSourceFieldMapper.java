package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Setter;

import static org.simpleflatmapper.util.Asserts.requireNonNull;


public final class ConstantSourceFieldMapper<S, T, P> implements FieldMapper<S, T> {
	
	public final ContextualGetter<? super S, ? extends P> getter;
	public final Setter<? super T, ? super P> setter;


	public ConstantSourceFieldMapper(final ContextualGetter<? super S, ? extends P> getter, final Setter<? super T, ? super P> setter) {
		this.getter = requireNonNull("getter", getter);
		this.setter = requireNonNull("setter", setter);
	}

	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
		final P value = getter.get(source, mappingContext);
		setter.set(target, value);
	}

    @Override
    public String toString() {
        return "ConstantSourceFieldMapperImpl{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
