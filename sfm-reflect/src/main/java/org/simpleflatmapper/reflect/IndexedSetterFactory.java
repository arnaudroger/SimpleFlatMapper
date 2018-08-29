package org.simpleflatmapper.reflect;

public interface IndexedSetterFactory<T, A> {
    <P> IndexedSetter<T, P> getIndexedSetter(A arg, Object... properties);
}
