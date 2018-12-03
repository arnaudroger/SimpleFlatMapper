package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.BooleanContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.setter.BooleanContextualSetter;
import org.simpleflatmapper.map.setter.ContextualSetterAdapter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;

public final class BooleanConstantSourceFieldMapper<S, T> implements FieldMapper<S, T> {

	public final BooleanContextualGetter<? super S> getter;
	public final BooleanSetter<? super T> setter;
	
 	public BooleanConstantSourceFieldMapper(final BooleanContextualGetter<? super S> getter, final BooleanSetter<? super T> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
		setter.setBoolean(target, getter.getBoolean(source, mappingContext));
	}

    @Override
    public String toString() {
        return "BooleanFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
