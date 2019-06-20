package org.simpleflatmapper.reflect;

public interface IndexedGetter<T, P> {
    P get(T target, int index) throws Exception;
}
