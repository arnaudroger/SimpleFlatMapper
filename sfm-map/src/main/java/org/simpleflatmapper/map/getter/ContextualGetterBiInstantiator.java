package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.BiInstantiator;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class ContextualGetterBiInstantiator<S, T> implements BiInstantiator<S, MappingContext<?>, T> {
    private final ContextualGetter<? super S, ? extends T> fieldMapperGetter;

    public ContextualGetterBiInstantiator(ContextualGetter<? super S, ? extends T> fieldMapperGetter) {
        this.fieldMapperGetter = requireNonNull("fieldMapperGetter", fieldMapperGetter);
    }

    @Override
    public T newInstance(S s, MappingContext<?> mappingContext) throws Exception {
        return fieldMapperGetter.get(s, mappingContext);
    }
}
