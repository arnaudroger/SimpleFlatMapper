package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Function;

import java.lang.reflect.Method;

public final class GenericBuilder<S, T>  {

    public static final Method BUILD;
    
    static {
        try {
            BUILD = GenericBuilder.class.getMethod("build");
        } catch (NoSuchMethodException e) {
            throw new Error("Unexpected error " + e, e);
        }
    }
    public final Object[] objects;
    public final BiInstantiator<Object[], Object, T> targetInstantiator;
    public final Setter<T, GenericBuilder<S, T>>[] targetFieldSetters;
    public final FieldMapper<S, GenericBuilder<S, T>>[] genericBuilderFieldMappers;

    public GenericBuilder(FieldMapper<S, GenericBuilder<S, T>>[] genericBuilderFieldMappers, BiInstantiator<Object[], Object, T> targetInstantiator, Setter<T, GenericBuilder<S, T>>[] targetFieldSetters) {
        this.objects = new Object[genericBuilderFieldMappers.length];
        this.genericBuilderFieldMappers = genericBuilderFieldMappers;
        this.targetInstantiator = targetInstantiator;
        this.targetFieldSetters = targetFieldSetters;
    }
    
    public T build() throws Exception {
        T t = targetInstantiator.newInstance(objects, this);
        for(Setter<T, GenericBuilder<S, T>> setter : targetFieldSetters) {
            setter.set(t, this);
        }
        return t;
    }

    public void mapFrom(S source, MappingContext<? super S> context) throws Exception {
        for(FieldMapper<S, GenericBuilder<S, T>> fm : genericBuilderFieldMappers) {
            fm.mapTo(source, this, context);
        }
    }

    public static <S, T> Function<GenericBuilder<S, T>, T> buildFunction() {
        return new GenericBuilderTransformFunction<S, T>();
    }

    private static class GenericBuilderTransformFunction<S, T> implements Function<GenericBuilder<S, T>, T> {
        @Override
        public T apply(GenericBuilder<S, T> o) {
            try {
                if (o == null) return null;
                return o.build();
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }
}
