package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Instantiator;

import java.lang.reflect.Method;
import java.util.Arrays;

public class GenericBuilder<T> {

    public static final Method BUILD;
    
    static {
        try {
            BUILD = GenericBuilder.class.getMethod("build");
        } catch (NoSuchMethodException e) {
            throw new Error("Unexpected error " + e, e);
        }
    }
    public final Object[] objects;
    public final BiInstantiator<Object[], Object, T> instantiator;
    public GenericBuilder(int n, BiInstantiator<Object[], Object, T> instantiator) {
        this.objects = new Object[n];
        this.instantiator = instantiator;
    }
    
    public T build() throws Exception {
        return instantiator.newInstance(objects, this);
    }


    public void reset() {
        Arrays.fill(objects, null);
    }
}
