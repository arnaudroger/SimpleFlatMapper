package org.sfm.reflect;

public interface IndexedGetter<T, P> {
    P get(T target, int index);
}
