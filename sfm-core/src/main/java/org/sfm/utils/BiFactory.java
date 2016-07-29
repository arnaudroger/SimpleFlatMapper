package org.sfm.utils;


public interface BiFactory<P1, P2, T> {
    T newInstance(P1 p1, P2 p2);
}
