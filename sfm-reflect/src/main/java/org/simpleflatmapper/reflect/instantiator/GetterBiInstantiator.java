package org.simpleflatmapper.reflect.instantiator;


import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;

public class GetterBiInstantiator<S1, S2, T> implements BiInstantiator<S1, S2, T> {
    private final Getter<S1, T> getter;

    public GetterBiInstantiator(Getter<S1, T> getter) {
        this.getter = getter;
    }

    @Override
    public T newInstance(S1 s1, S2 s2) throws Exception {
        return getter.get(s1);
    }
}
