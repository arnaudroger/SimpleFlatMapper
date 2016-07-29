package org.sfm.utils;


public interface UnaryFactory<P, T> {
    T newInstance(P p);
}
