package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.reflect.getter.GetterFactory;

import java.lang.reflect.Type;

public class ContextualSetterFactoryAdapter<T, K> implements ContextualSetterFactory<T, K> {
    
    private final SetterFactory<T, K> delegate;

    public ContextualSetterFactoryAdapter(SetterFactory<T, K> delegate) {
        this.delegate = delegate;
    }


    @Override
    public <P> ContextualSetter<T, P> getSetter(K arg, ContextFactoryBuilder contextFactoryBuilder) {
        Setter<T, P> setter = delegate.getSetter(arg);
        if (setter == null) return null;
        return ContextualSetterAdapter.of(setter);
    }
}
