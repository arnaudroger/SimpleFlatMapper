package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.BiInstantiator;

public class BiInstantiatorFieldMapperGetter<S, T> implements FieldMapperGetter<S, T> {
    private final BiInstantiator<? super S, ? super MappingContext<?>, ? extends T> biInstantiator;

    public BiInstantiatorFieldMapperGetter(BiInstantiator<? super S, ? super MappingContext<?>, ? extends T> biInstantiator) {
        this.biInstantiator = biInstantiator;
    }

    @Override
    public T get(S s, MappingContext<?> context) throws Exception {
        return biInstantiator.newInstance(s, context);
    }
}
