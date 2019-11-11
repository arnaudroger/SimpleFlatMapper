package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactory;

import java.lang.reflect.Type;

public class ContextualGetterFactoryAdapter<T, K> implements ContextualGetterFactory<T, K> {
    
    public final GetterFactory<? super T, K> delegate;

    public ContextualGetterFactoryAdapter(GetterFactory<? super T, K> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <P> ContextualGetter<T, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
        Getter<? super T, P> getter = delegate.newGetter(target, key, properties);
        if (getter == null) return null;
        return ContextualGetterAdapter.of(getter);
    }

    @Override
    public String toString() {
        return "ContextualGetterFactoryAdapter{" +
                "delegate=" + delegate +
                '}';
    }
}
