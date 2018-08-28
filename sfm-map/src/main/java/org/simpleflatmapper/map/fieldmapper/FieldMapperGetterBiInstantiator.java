package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.BiInstantiator;

public class FieldMapperGetterBiInstantiator<S, T> implements BiInstantiator<S, MappingContext<?>, T> {
    private final FieldMapperGetter<? super S, ? extends T> fieldMapperGetter;

    public FieldMapperGetterBiInstantiator(FieldMapperGetter<? super S, ? extends T> fieldMapperGetter) {
        this.fieldMapperGetter = fieldMapperGetter;
    }

    @Override
    public T newInstance(S s, MappingContext<?> mappingContext) throws Exception {
        return fieldMapperGetter.get(s, mappingContext);
    }
}
