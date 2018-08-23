package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Predicate;

import java.util.List;

public class NullChecker<S, K> implements Predicate<S> {

    private final List<K> keys;
    private final KeySourceGetter<K, ? super S> keySourceGetter;

    public NullChecker(List<K> keys, KeySourceGetter<K, ? super S> keySourceGetter) {
        this.keys = keys;
        this.keySourceGetter = keySourceGetter;
    }

    @Override
    public boolean test(S s) {
        try {
            if (keys.isEmpty()) return false;
            for (int i = 0; i < keys.size(); i++) {
                if (keySourceGetter.getValue(keys.get(i), s) != null) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
            throw new IllegalStateException();
        }
    }
}
