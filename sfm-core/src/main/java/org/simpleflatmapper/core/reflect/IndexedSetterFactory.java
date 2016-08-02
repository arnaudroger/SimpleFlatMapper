package org.simpleflatmapper.core.reflect;


public interface IndexedSetterFactory<T, A> {
    <P> IndexedSetter<T, P> getIndexedSetter(A arg);
}
