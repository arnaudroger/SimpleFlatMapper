package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.context.KeyAndPredicate;
import org.simpleflatmapper.map.context.KeyDefinition;
import org.simpleflatmapper.map.context.KeySourceGetter;

import java.lang.reflect.Array;
import java.util.List;

public class KeyDefinitionBuilder<S, K> {
    private final KeySourceGetter<K, ? super S> keySourceGetter;
    private final List<KeyAndPredicate<S, K>> keyAndPredicates;
    private final int index;

    public KeyDefinitionBuilder(List<KeyAndPredicate<S, K>> keyAndPredicates, KeySourceGetter<K, ? super S> keySourceGetter, int index) {
        this.keyAndPredicates = keyAndPredicates;
        this.keySourceGetter = keySourceGetter;
        this.index = index;
    }


    public KeyDefinitionBuilder<S, K> asChild(int currentIndex) {
        return new KeyDefinitionBuilder<S, K>(keyAndPredicates,  keySourceGetter, currentIndex);
    }

    public static <S, K> KeyDefinition<S, K>[] toKeyDefinitions(KeyDefinitionBuilder<S, K>[] siblings) {
        KeyDefinition<S, K>[] keyDefinitions = new KeyDefinition[siblings.length];
        for(KeyDefinitionBuilder<S, K> builder : siblings) {
            KeyDefinition<S, K> keyDefinition = new KeyDefinition<S, K>(toArray(builder.keyAndPredicates), builder.keySourceGetter, builder.index);
            keyDefinitions[builder.index]= keyDefinition;
        }
        return keyDefinitions;
    }

    private static <K> K[] toArray(List<K> keys) {
        if (keys.size() == 0) return null;
        else return keys.toArray((K[]) Array.newInstance(keys.get(0).getClass(), 0));
    }

    public List<KeyAndPredicate<S, K>> getKeyAndPredicates() {
        return keyAndPredicates;
    }
}
