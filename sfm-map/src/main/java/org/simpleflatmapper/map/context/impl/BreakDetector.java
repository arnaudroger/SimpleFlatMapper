package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.context.Key;
import org.simpleflatmapper.map.context.KeyDefinition;


public class BreakDetector<S>  {

    private final KeyDefinition<S, ?> definition;
    private final KeyObjectStore cache;

    private Key currentKey;


    public BreakDetector(KeyDefinition<S, ?> definition) {
        this.definition = emptyToNull(definition);
        if (this.definition == null) {
            cache = null;
        } else {
            cache = new KeyObjectStore();
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

        Key oldKey = currentKey;
        currentKey = definition.getValues(source);

        return oldKey == null || !oldKey.equals(currentKey);
    }

    public void handleSource(S source) throws MappingException {
        if (definition == null) {
            return;
        }
        currentKey = definition.getValues(source);
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

    public void markRootAsBroken() {
        if (definition != null) {
            currentKey = null;
            cache.clear();
        }
    }
}
