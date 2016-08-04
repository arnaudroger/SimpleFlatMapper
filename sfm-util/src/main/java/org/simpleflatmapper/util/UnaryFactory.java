package org.simpleflatmapper.util;


public interface UnaryFactory<P, T> {
    T newInstance(P p);
}
