package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public class BoxedLongFieldMapperGetter<T> implements LongFieldMapperGetter<T>, FieldMapperGetter<T, Long> {


    private final FieldMapperGetter<? super T, ? extends Long> delegate;

    public BoxedLongFieldMapperGetter(FieldMapperGetter<? super T, ? extends Long> delegate) {
        this.delegate = delegate;
    }

    @Override
    public long getLong(T target, MappingContext<?> mappingContext) throws Exception {
        final Long value = get(target, mappingContext);
        if (value != null) {
            return value.longValue();
        }
        return 0;
    }

    @Override
    public Long get(T target, MappingContext<?> context) throws Exception {
        return delegate.get(target, context);
    }
}
