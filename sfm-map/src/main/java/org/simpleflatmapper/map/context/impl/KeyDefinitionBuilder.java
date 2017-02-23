package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.context.KeyDefinition;
import org.simpleflatmapper.map.context.KeySourceGetter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KeyDefinitionBuilder<S, K> {
    private final KeySourceGetter<K, S> keySourceGetter;
    private final List<K> keys;
    private final int index;

    private final ArrayList<KeyDefinitionBuilder<S, K>> children = new ArrayList<KeyDefinitionBuilder<S, K>>();
    private final boolean root;

    public KeyDefinitionBuilder(List<K> keys, KeySourceGetter<K, S> keySourceGetter, KeyDefinitionBuilder<S, K> parent, int index, boolean root) {
        this.keys = keys;
        this.keySourceGetter = keySourceGetter;
        this.root = root;
        if (parent != null)
            parent.addChild(this);
        this.index = index;
    }


    public KeyDefinitionBuilder<S, K> asChild(int currentIndex) {
        return new KeyDefinitionBuilder<S, K>(keys, keySourceGetter, this, currentIndex, false);
    }

    private void addChild(KeyDefinitionBuilder<S, K> keyDefinition) {
        children.add(keyDefinition);
    }

    public static <S, K> KeyDefinition<S, K>[] toKeyDefinitions(KeyDefinitionBuilder<S, K>[] siblings) {
        KeyDefinition<S, K>[] keyDefinitions = new KeyDefinition[siblings.length];
        for(KeyDefinitionBuilder<S, K> builder : siblings) {
            defineBuilder(builder, keyDefinitions);
        }
        return keyDefinitions;
    }

    private static <S, K> KeyDefinition<S, K> defineBuilder(KeyDefinitionBuilder<S, K> builder, KeyDefinition<S, K>[] keyDefinitions) {
        if (keyDefinitions[builder.index] != null) {
            return keyDefinitions[builder.index];
        }

        List<KeyDefinition<S, K>> children = new ArrayList<KeyDefinition<S, K>>();

        for(KeyDefinitionBuilder<S, K> child : builder.children) {
            children.add(defineBuilder(child, keyDefinitions));
        }

        KeyDefinition[] keyChildren =  null;

        if (!children.isEmpty())
            keyChildren = children.toArray(new KeyDefinition[0]);

        KeyDefinition<S, K> keyDefinition = new KeyDefinition<S, K>(toK(builder.keys), builder.keySourceGetter, keyChildren, builder.index, builder.root);

        keyDefinitions[builder.index]= keyDefinition;

        return keyDefinition;
    }

    private static <K> K[] toK(List<K> keys) {
        if (keys.size() == 0) return null;
        else return keys.toArray((K[]) Array.newInstance(keys.get(0).getClass(), 0));
    }

    public List<K> getKeys() {
        return keys;
    }

    public boolean isRoot() {
        return root;
    }
}
