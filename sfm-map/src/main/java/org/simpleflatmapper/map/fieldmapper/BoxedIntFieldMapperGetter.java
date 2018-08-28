package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public class BoxedIntFieldMapperGetter<T> implements IntFieldMapperGetter<T>, FieldMapperGetter<T, Integer> {


    private final FieldMapperGetter<? super T, ? extends Integer> delegate;

    public BoxedIntFieldMapperGetter(FieldMapperGetter<? super T, ? extends Integer> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getInt(T target, MappingContext<?> mappingContext) throws Exception {
        final Integer value = get(target, mappingContext);
        if (value != null) {
            return value.intValue();
        }
        return 0;
    }

    @Override
    public Integer get(T target, MappingContext<?> context) throws Exception {
        return delegate.get(target, context);
    }
}
