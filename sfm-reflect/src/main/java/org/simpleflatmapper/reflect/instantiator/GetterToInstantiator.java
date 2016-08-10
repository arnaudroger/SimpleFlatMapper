package org.simpleflatmapper.reflect.instantiator;


import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;

public class GetterToInstantiator<S, T> implements Instantiator<S, T> {
    private final Getter<S, T> getter;

    public GetterToInstantiator(Getter<S, T> getter) {
        this.getter = getter;
    }

    @Override
    public T newInstance(S s) throws Exception {
        return getter.get(s);
    }
}
