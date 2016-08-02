package org.simpleflatmapper.core.utils;


public interface UnaryFactory<P, T> {
    T newInstance(P p);
}
