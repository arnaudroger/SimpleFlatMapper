package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ErrorHelper;

public class FieldMapperGetterBiFunction<S, T> implements BiFunction<S, MappingContext<?>, T> {
    private final FieldMapperGetter<? super S, ? extends T> fieldMapperGetter;

    public FieldMapperGetterBiFunction(FieldMapperGetter<? super S, ? extends T> fieldMapperGetter) {
        this.fieldMapperGetter = fieldMapperGetter;
    }

    @Override
    public T apply(S s, MappingContext<?> mappingContext) {
        try {
            return fieldMapperGetter.get(s, mappingContext);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }
}
