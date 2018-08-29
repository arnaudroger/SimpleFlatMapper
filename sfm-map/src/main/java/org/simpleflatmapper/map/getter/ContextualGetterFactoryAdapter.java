package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactory;

import java.lang.reflect.Type;

public class ContextualGetterFactoryAdapter<T, K> implements ContextualGetterFactory<T, K> {
    
    private final GetterFactory<T, K> delegate;

    public ContextualGetterFactoryAdapter(GetterFactory<T, K> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <P> ContextualGetter<T, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, K> mappingContextFactoryBuilder, Object... properties) {
        Getter<T, P> getter = delegate.newGetter(target, key, properties);
        if (getter == null) return null;
        return ContextualGetterAdapter.of(getter);
    }
}
