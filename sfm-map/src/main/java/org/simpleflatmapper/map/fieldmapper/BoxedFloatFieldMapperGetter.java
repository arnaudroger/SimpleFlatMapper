package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public class BoxedFloatFieldMapperGetter<T> implements FloatFieldMapperGetter<T>, FieldMapperGetter<T, Float> {


    private final FieldMapperGetter<? super T, ? extends Float> delegate;

    public BoxedFloatFieldMapperGetter(FieldMapperGetter<? super T, ? extends Float> delegate) {
        this.delegate = delegate;
    }

    @Override
    public float getFloat(T target, MappingContext<?> mappingContext) throws Exception {
        final Float value = get(target, mappingContext);
        if (value != null) {
            return value.floatValue();
        }
        return 0;
    }

    @Override
    public Float get(T target, MappingContext<?> context) throws Exception {
        return delegate.get(target, context);
    }
}
