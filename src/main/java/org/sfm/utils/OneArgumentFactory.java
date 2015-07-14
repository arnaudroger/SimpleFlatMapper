package org.sfm.utils;


public interface OneArgumentFactory<P, T> {
    T newInstance(P p);
}
