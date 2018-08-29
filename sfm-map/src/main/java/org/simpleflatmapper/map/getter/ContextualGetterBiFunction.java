package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ErrorHelper;

public class ContextualGetterBiFunction<S, T> implements BiFunction<S, Context, T> {
    private final ContextualGetter<? super S, ? extends T> fieldMapperGetter;

    public ContextualGetterBiFunction(ContextualGetter<? super S, ? extends T> fieldMapperGetter) {
        this.fieldMapperGetter = fieldMapperGetter;
    }

    @Override
    public T apply(S s, Context mappingContext) {
        try {
            return fieldMapperGetter.get(s, mappingContext);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }
}
