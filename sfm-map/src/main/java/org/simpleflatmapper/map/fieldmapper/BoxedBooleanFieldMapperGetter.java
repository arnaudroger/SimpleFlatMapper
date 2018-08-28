package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public class BoxedBooleanFieldMapperGetter<T> implements BooleanFieldMapperGetter<T>, FieldMapperGetter<T, Boolean> {


    private final FieldMapperGetter<? super T, ? extends Boolean> delegate;

    public BoxedBooleanFieldMapperGetter(FieldMapperGetter<? super T, ? extends Boolean> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean getBoolean(T target, MappingContext<?> mappingContext) throws Exception {
        final Boolean bool = get(target, mappingContext);
        if (bool != null) {
            return bool.booleanValue();
        }
        return false;
    }

    @Override
    public Boolean get(T target, MappingContext<?> context) throws Exception {
        return delegate.get(target, context);
    }
}
