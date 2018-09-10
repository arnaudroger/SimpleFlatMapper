package org.simpleflatmapper.map.context;

import org.simpleflatmapper.map.context.impl.MultiValueKey;
import org.simpleflatmapper.map.context.impl.SingleValueKey;
import org.simpleflatmapper.util.ErrorHelper;

public class KeyDefinition<S, K> {
    private final KeySourceGetter<K, ? super S> keySourceGetter;

    private final K[] keys;
    private final K singleKey;

    private final boolean empty;

    private final int index;

    public KeyDefinition(K[] keys, KeySourceGetter<K, ? super S> keySourceGetter, int index) {
        this.singleKey = getSingleKey(keys);
        if (singleKey == null) {
            this.keys = keys;
        } else {
            this.keys = null;
        }

        this.keySourceGetter = keySourceGetter;
        this.empty = keys == null || keys.length == 0;
        this.index = index;
    }

    private static <K> K getSingleKey(K[] keys) {
        //IFJAVA8_START
        if (keys != null && keys.length == 1) return keys[0];
        //IFJAVA8_END
        return null;
    }

    public boolean isEmpty() {
        return empty;
    }

    public Key getValues(S source) {
        if (empty) throw new IllegalStateException("cannot get value on empty keys");
        try {
            if (singleKey != null) {
                return singleValueKeys(source);
            } else {
                return multiValueKeys(source);
            }
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    private Key singleValueKeys(S source) throws Exception {
        return new SingleValueKey(keySourceGetter.getValue(singleKey, source));
    }

    private Key multiValueKeys(S source) throws Exception {
        Object[] values = new Object[keys.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = keySourceGetter.getValue(keys[i], source);
        }
        return new MultiValueKey(values);
    }

    public int getIndex() {
        return index;
    }
}
