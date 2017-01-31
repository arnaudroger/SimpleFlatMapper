package org.simpleflatmapper.reflect;

public class BiToUnaryInstantiator<S1, S2, T> implements BiInstantiator<S1, S2, T> {

    private final Instantiator<S1, T> instantiator;

    public BiToUnaryInstantiator(Instantiator<S1, T> instantiator) {
        this.instantiator = instantiator;
    }

    @Override
    public T newInstance(S1 s1, S2 s2) throws Exception {
        return instantiator.newInstance(s1);
    }
}
