package org.simpleflatmapper.core.reflect;


public interface SetterFactory<T, A> {
    <P> Setter<T, P> getSetter(A arg);
}
