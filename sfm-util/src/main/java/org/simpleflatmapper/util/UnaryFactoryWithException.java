package org.simpleflatmapper.util;


public interface UnaryFactoryWithException<P, T, E extends Exception> {
    T newInstance(P p) throws E;
}
