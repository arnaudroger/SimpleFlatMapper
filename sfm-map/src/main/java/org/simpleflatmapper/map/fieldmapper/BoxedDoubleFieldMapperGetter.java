package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public class BoxedDoubleFieldMapperGetter<T> implements DoubleFieldMapperGetter<T>, FieldMapperGetter<T, Double> {


    private final FieldMapperGetter<? super T, ? extends Double> delegate;

    public BoxedDoubleFieldMapperGetter(FieldMapperGetter<? super T, ? extends Double> delegate) {
        this.delegate = delegate;
    }

    @Override
    public double getDouble(T target, MappingContext<?> mappingContext) throws Exception {
        final Double value = get(target, mappingContext);
        if (value != null) {
            return value.doubleValue();
        }
        return 0;
    }

    @Override
    public Double get(T target, MappingContext<?> context) throws Exception {
        return delegate.get(target, context);
    }
}
