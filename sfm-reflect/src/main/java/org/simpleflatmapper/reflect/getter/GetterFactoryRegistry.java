package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetterFactoryRegistry<S, K> {

    private final Map<Class<?>, GetterFactory<S, K>> factoryPerType =
            new HashMap<Class<?>, GetterFactory<S, K>>();


    public final void put(Class<?> target, GetterFactory<S, K> getterFactory ){
        factoryPerType.put(target, getterFactory);
        
    }

    public final GetterFactory<S, K> findFactoryFor(Type targetType) {
        Class<?> target = TypeHelper.toClass(targetType);
        GetterFactory<S, K> getterFactory = factoryPerType.get(target);

        if (getterFactory != null) {
            return getterFactory;
        }

        return null;
    }

    public void mapFromTo(Class<?> target, Class<?> source) {
        GetterFactory<S, K> getterFactory = factoryPerType.get(source);
        if (getterFactory == null) throw new IllegalStateException("No getter factory defined for " + source);
        put(target, getterFactory);
    }
    
}
