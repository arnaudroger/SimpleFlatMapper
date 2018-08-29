package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.getter.ShortContextualGetter;
import org.simpleflatmapper.map.setter.ContextualSetterAdapter;
import org.simpleflatmapper.map.setter.ShortContextualSetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;

public final class ShortFieldMapper<S, T> implements FieldMapper<S, T> {

	private final ShortContextualGetter<? super S> getter;
	private final ShortContextualSetter<? super T> setter;
	
 	public ShortFieldMapper(final ShortContextualGetter<? super S> getter, final ShortSetter<? super T> setter) {
		this.getter = getter;
		this.setter = ContextualSetterAdapter.of(setter);
	}
	public ShortFieldMapper(final ShortGetter<? super S> getter, final ShortContextualSetter<? super T> setter) {
		this.getter = ContextualGetterAdapter.of(getter);
		this.setter = setter;
	}
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
        setter.setShort(target, getter.getShort(source, mappingContext), mappingContext);
	}

    @Override
    public String toString() {
        return "ShortFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
