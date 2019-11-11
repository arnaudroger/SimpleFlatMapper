package org.simpleflatmapper.map.setter;


import org.simpleflatmapper.converter.ContextFactoryBuilder;

public interface ContextualSetterFactory<T, A> {
    <P> ContextualSetter<T, P> getSetter(A arg, ContextFactoryBuilder contextFactoryBuilder);
}
