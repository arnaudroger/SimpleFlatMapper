package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public class BoxedShortFieldMapperGetter<T> implements ShortFieldMapperGetter<T>, FieldMapperGetter<T, Short> {


    private final FieldMapperGetter<? super T, ? extends Short> delegate;

    public BoxedShortFieldMapperGetter(FieldMapperGetter<? super T, ? extends Short> delegate) {
        this.delegate = delegate;
    }

    @Override
    public short getShort(T target, MappingContext<?> mappingContext) throws Exception {
        final Short value = get(target, mappingContext);
        if (value != null) {
            return value.shortValue();
        }
        return 0;
    }

    @Override
    public Short get(T target, MappingContext<?> context) throws Exception {
        return delegate.get(target, context);
    }
}
