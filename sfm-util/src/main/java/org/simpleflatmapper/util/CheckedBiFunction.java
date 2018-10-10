package org.simpleflatmapper.util;


public interface CheckedBiFunction<P1, P2, T> {
    T apply(P1 p1, P2 p2) throws Exception;
}
