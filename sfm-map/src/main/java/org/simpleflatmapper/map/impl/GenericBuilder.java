package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Setter;

import java.lang.reflect.Method;
import java.util.Arrays;

public final class GenericBuilder<S, T> implements FieldMapper<S, T> {

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

    @Override
    public void mapTo(S source, T target, MappingContext<? super S> context) throws Exception {
        for(FieldMapper<S, GenericBuilder<S, T>> fm : genericBuilderFieldMappers) {
            fm.mapTo(source, this, context);
        }
    }
}
