package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.BooleanContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.setter.BooleanContextualSetter;
import org.simpleflatmapper.map.setter.ContextualSetterAdapter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;

public final class BooleanConstantTargetFieldMapper<S, T> implements FieldMapper<S, T> {

	private final BooleanGetter<? super S> getter;
	private final BooleanContextualSetter<? super T> setter;
	
	public BooleanConstantTargetFieldMapper(final BooleanGetter<? super S> getter, final BooleanContextualSetter<? super T> setter) {
		this.getter = getter;
		this.setter = setter;
	}


	@Override
	public void mapTo(final S source, final T target, final MappingContext<? super S> mappingContext) throws Exception {
		setter.setBoolean(target, getter.getBoolean(source), mappingContext);
	}

    @Override
    public String toString() {
        return "BooleanFieldMapper{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
