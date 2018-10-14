package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.context.KeyAndPredicate;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Predicate;

import java.util.List;

public class NullChecker<S, K> implements Predicate<S> {

    private final List<KeyAndPredicate<S, K>> keys;
    private final KeySourceGetter<K, ? super S> keySourceGetter;

    public NullChecker(List<KeyAndPredicate<S, K>> keys, KeySourceGetter<K, ? super S> keySourceGetter) {
        this.keys = keys;
        this.keySourceGetter = keySourceGetter;
    }

    @Override
    public boolean test(S s) {
        try {
            boolean empty = true;
            for (KeyAndPredicate<S, K> keyAndPredicate : keys) {
                if (keyAndPredicate.test(s)) {
                    empty = false;
                    if (keySourceGetter.getValue(keyAndPredicate.key, s) != null) {
                        return false;
                    }
                }
            }
            return !empty;
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
            throw new IllegalStateException();
        }
    }
}
