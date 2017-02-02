package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ErrorHelper;

public final class BiFunctionGetter<S1, S2, T> implements BiFunction<S1, S2, T> {
    private final Getter<? super S1, ? extends T> getter;

    public BiFunctionGetter(Getter<? super S1, ? extends  T> getter) {
        this.getter = getter;
    }

    @Override
    public T apply(S1 s1, S2 s2) {
        try {
            return getter.get(s1);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    public Getter<? super S1, ? extends T> getGetter() {
        return getter;
    }
}
