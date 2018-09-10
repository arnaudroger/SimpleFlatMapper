package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.context.KeyDefinition;
import org.simpleflatmapper.map.context.KeySourceGetter;

import java.lang.reflect.Array;
import java.util.List;

public class KeyDefinitionBuilder<S, K> {
    private final KeySourceGetter<K, ? super S> keySourceGetter;
    private final List<K> keys;
    private final int index;

    public KeyDefinitionBuilder(List<K> keys, KeySourceGetter<K, ? super S> keySourceGetter, int index) {
        this.keys = keys;
        this.keySourceGetter = keySourceGetter;
        this.index = index;
    }


    public KeyDefinitionBuilder<S, K> asChild(int currentIndex) {
        return new KeyDefinitionBuilder<S, K>(keys, keySourceGetter, currentIndex);
    }

    public static <S, K> KeyDefinition<S, K>[] toKeyDefinitions(KeyDefinitionBuilder<S, K>[] siblings) {
        KeyDefinition<S, K>[] keyDefinitions = new KeyDefinition[siblings.length];
        for(KeyDefinitionBuilder<S, K> builder : siblings) {
            KeyDefinition<S, K> keyDefinition = new KeyDefinition<S, K>(toK(builder.keys), builder.keySourceGetter, builder.index);
            keyDefinitions[builder.index]= keyDefinition;
        }
        return keyDefinitions;
    }

    private static <K> K[] toK(List<K> keys) {
        if (keys.size() == 0) return null;
        else return keys.toArray((K[]) Array.newInstance(keys.get(0).getClass(), 0));
    }

    public List<K> getKeys() {
        return keys;
    }
}
