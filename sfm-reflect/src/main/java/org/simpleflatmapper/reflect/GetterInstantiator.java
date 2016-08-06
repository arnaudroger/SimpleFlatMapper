package org.simpleflatmapper.reflect;


public class GetterInstantiator<S, T> implements Instantiator<S, T> {
    private final Getter<S, T> getter;

    public GetterInstantiator(Getter<S, T> getter) {
        this.getter = getter;
    }

    @Override
    public T newInstance(S s) throws Exception {
        return getter.get(s);
    }
}
