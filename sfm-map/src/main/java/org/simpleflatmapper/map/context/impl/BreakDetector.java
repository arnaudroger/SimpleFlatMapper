package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.context.Key;
import org.simpleflatmapper.map.context.KeyDefinition;

import java.util.HashMap;

public class BreakDetector<S>  {

    private final KeyDefinition<S, ?> definition;
    private final HashMap<Key, Object> cache;
    private final BreakDetector<S>[] siblings;
    private final boolean root;

    private Key currentKey;


    public BreakDetector(KeyDefinition<S, ?> definition, BreakDetector<S>[] siblings) {
        this.definition = emptyToNull(definition);
        this.siblings = siblings;
        this.root = definition.isRoot();
        if (this.definition == null) {
            cache = null;
        } else {
            cache = new HashMap<Key, Object>();
        }
    }

    private static <S> KeyDefinition<S, ?> emptyToNull(KeyDefinition<S, ?> definition) {
        if (definition != null) {
            if (definition.isEmpty()) {
                return null;
            }
        }
        return definition;
    }

    public boolean broke(S source) throws MappingException {
        if (definition == null) {
            return true;
        }

        boolean b = false;

        Key oldKey = currentKey;

        currentKey = definition.getValues(source);

        if (oldKey == null) {
            b = true;
        } else if (!oldKey.equals(currentKey)) {
            b = true;
            markChildrenHasBroken(root);
        }

        callBrokeOnChildren(source);

        return b;
    }

    private void callBrokeOnChildren(S source) {
        KeyDefinition<S, ?>[] children = definition.getChildren();
        if (children == null) return;
        for(KeyDefinition<S, ?> keyDefinition : children) {
            siblings[keyDefinition.getIndex()].broke(source);
        }
    }

    private void markChildrenHasBroken(boolean root) {
        KeyDefinition<S, ?>[] children = definition.getChildren();
        if (children == null) return;
        for(KeyDefinition<S, ?> keyDefinition : children) {
            siblings[keyDefinition.getIndex()].markAsBroken(root);
        }
    }

    public void setValue(Object value) {
        if (definition != null) {
            if (currentKey == null)
                throw new IllegalStateException("Invalid state currentKey is null");
            cache.put(currentKey, value);
        }
    }

    public Object getValue() {
        if (definition != null) {
            if (currentKey == null)
                throw new IllegalStateException("Invalid state currentKey is null");
            return cache.get(currentKey);
        }
        return null;
    }

    public void markAsBroken(boolean root) {
        if (definition != null) {
            currentKey = null;
            if (root) {
                cache.clear();
            }
            markChildrenHasBroken(root);
        }
    }
}
