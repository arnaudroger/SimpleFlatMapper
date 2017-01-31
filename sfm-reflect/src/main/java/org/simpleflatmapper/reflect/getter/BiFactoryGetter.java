package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.BiFactory;
import org.simpleflatmapper.util.ErrorHelper;

public final class BiFactoryGetter<S1, S2, T> implements BiFactory<S1, S2, T> {
    private final Getter<? super S1, ? extends T> getter;

    public BiFactoryGetter(Getter<? super S1, ? extends  T> getter) {
        this.getter = getter;
    }

    @Override
    public T newInstance(S1 s1, S2 s2) {
        try {
            return getter.get(s1);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }
}
