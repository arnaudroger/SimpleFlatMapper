package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.map.setter.ContextualSetterAdapter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;

import static org.simpleflatmapper.util.Asserts.requireNonNull;


public final class ConstantSourceFieldMapper<S, T, P> implements FieldMapper<S, T> {
	
	private final ContextualGetter<? super S, ? extends P> getter;
	private final Setter<? super T, ? super P> setter;


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
