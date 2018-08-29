package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.getter.LongContextualGetter;
import org.simpleflatmapper.map.setter.ContextualSetterAdapter;
import org.simpleflatmapper.map.setter.LongContextualSetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.LongSetter;

public final class LongFieldMapper<S, T> implements FieldMapper<S, T> {

	private final LongContextualGetter<? super S> getter;
	private final LongContextualSetter<? super T> setter;
	
 	public LongFieldMapper(final LongContextualGetter<? super S> getter, final LongSetter<? super T> setter) {
		this.getter = getter;
		this.setter = ContextualSetterAdapter.of(setter);
	}
	public LongFieldMapper(final LongGetter<? super S> getter, final LongContextualSetter<? super T> setter) {
		this.getter = ContextualGetterAdapter.of(getter);
		this.setter = setter;
	}
	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
		setter.setLong(target, getter.getLong(source, mappingContext), mappingContext);
	}

    @Override
    public String toString() {
        return "LongFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
