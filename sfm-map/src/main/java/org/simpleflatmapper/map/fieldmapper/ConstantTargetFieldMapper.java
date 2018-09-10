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


public final class ConstantTargetFieldMapper<S, T, P> implements FieldMapper<S, T> {
	
	private final Getter<? super S, ? extends P> getter;
	private final ContextualSetter<? super T, ? super P> setter;


	public ConstantTargetFieldMapper(final Getter<? super S, ? extends P> getter, final ContextualSetter<? super T, ? super P> setter) {
		this.getter = requireNonNull("getter", getter);
		this.setter = requireNonNull("setter", setter);
	}
	
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
		final P value = getter.get(source);
		setter.set(target, value, mappingContext);
	}

    @Override
    public String toString() {
        return "FieldMapperImpl{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
