package org.sfm.map.context;

import org.sfm.utils.ErrorHelper;

import java.util.List;

public class KeysDefinition<S, K> {
    private final KeySourceGetter<K, S> keySourceGetter;
    private final List<K> keys;
    private final int parentIndex;

    public KeysDefinition(List<K> keys, KeySourceGetter<K, S> keySourceGetter, int parentIndex) {
        this.keys = keys;
        this.keySourceGetter = keySourceGetter;
        this.parentIndex = parentIndex;
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }

    public Object[] getValues(S source) {
        try {
            Object[] values = new Object[keys.size()];
            for (int i = 0; i < values.length; i++) {
                values[i] = keySourceGetter.getValue(keys.get(i), source);
            }
            return values;
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    public int getParentIndex() {
        return parentIndex;
    }
}
