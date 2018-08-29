package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.getter.IntContextualGetter;
import org.simpleflatmapper.map.setter.ContextualSetterAdapter;
import org.simpleflatmapper.map.setter.IntContextualSetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.IntSetter;

public final class IntFieldMapper<S, T> implements FieldMapper<S, T> {

	private final IntContextualGetter<? super S> getter;
	private final IntContextualSetter<? super T> setter;
	
 	public IntFieldMapper(final IntContextualGetter<? super S> getter, final IntSetter<? super T> setter) {
		this.getter = getter;
		this.setter = ContextualSetterAdapter.of(setter);
	}

	public IntFieldMapper(final IntGetter<? super S> getter, final IntContextualSetter<? super T> setter) {
		this.getter = ContextualGetterAdapter.of(getter);
		this.setter = setter;
	}

	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
        setter.setInt(target, getter.getInt(source, mappingContext), mappingContext);
	}

    @Override
    public String toString() {
        return "IntFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
