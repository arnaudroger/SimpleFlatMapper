package org.simpleflatmapper.map.context;

import org.simpleflatmapper.map.context.impl.MultiValueKey;
import org.simpleflatmapper.map.context.impl.SingleValueKey;
import org.simpleflatmapper.util.ErrorHelper;

public class KeyDefinition<S, K> {
    public static final Key NOT_EQUALS = new Key() {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };
    private final KeySourceGetter<K, ? super S> keySourceGetter;

    private final KeyAndPredicate<S, K>[] keyAndPredicates;
    private final KeyAndPredicate<S, K> singleKeyAndPredicate;

    private final boolean empty;

    private final int index;

    public KeyDefinition(KeyAndPredicate<S, K>[] keyAndPredicates, KeySourceGetter<K, ? super S> keySourceGetter, int index) {
        this.singleKeyAndPredicate = getSingle(keyAndPredicates);
        if (singleKeyAndPredicate == null) {
            this.keyAndPredicates = keyAndPredicates;
        } else {
            this.keyAndPredicates = null;
        }
        this.keySourceGetter = keySourceGetter;
        this.empty = keyAndPredicates == null || keyAndPredicates.length == 0;
        this.index = index;
    }

    private static <K> K getSingle(K[] array) {
        //IFJAVA8_START
        if (array != null && array.length == 1) return array[0];
        //IFJAVA8_END
        return null;
    }

    public boolean isEmpty() {
        return empty;
    }

    public Key getValues(S source) {
        if (empty) throw new IllegalStateException("cannot get value on empty keys");
        try {
            if (singleKeyAndPredicate != null) {
                return singleValueKeys(source);
            } else {
                return multiValueKeys(source);
            }
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    private Key singleValueKeys(S source) throws Exception {
        Object value;
        if (singleKeyAndPredicate.test(source)) {
            value = keySourceGetter.getValue(singleKeyAndPredicate.key, source);
        } else {
            return NOT_EQUALS;
        }
        return new SingleValueKey(value);
    }

    private Key multiValueKeys(S source) throws Exception {
        Object[] values = new Object[keyAndPredicates.length];
        
        boolean empty = true;
        for (int i = 0; i < values.length; i++) {
            Object value = null;
            KeyAndPredicate<S, K> keyAndPredicate = keyAndPredicates[i];
            if (keyAndPredicate.test(source)) {
                empty = false;       
                value = keySourceGetter.getValue(keyAndPredicate.key, source);
            }
            values[i] = value;
        }
        if (empty) return NOT_EQUALS;
        return new MultiValueKey(values);
    }

    public int getIndex() {
        return index;
    }
}
