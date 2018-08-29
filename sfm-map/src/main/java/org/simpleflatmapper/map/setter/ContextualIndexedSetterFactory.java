package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.ContextFactoryBuilder;

public interface ContextualIndexedSetterFactory<T, A> {
    <P> ContextualIndexedSetter<T, P> getIndexedSetter(A arg, ContextFactoryBuilder contextFactoryBuilder, Object... properties);
}
