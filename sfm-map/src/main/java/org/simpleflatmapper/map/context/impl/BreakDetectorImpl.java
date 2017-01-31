package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.BreakDetector;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.context.KeysDefinition;

import java.util.Arrays;

public class BreakDetectorImpl<S, K> implements BreakDetector<S> {

    private final KeysDefinition<S, K> definition;
    private final BreakDetector<S>[] family;
    private Object[] lastValues;

    private boolean isBroken = true;


    public BreakDetectorImpl(KeysDefinition<S, K> definition, BreakDetector<S>[] family) {
        this.definition = definition;
        this.family = family;
    }

    @Override
    public void handle(S source) throws MappingException {
        if (definition.isEmpty()) {
            return;
        }

        Object[] newValues = definition.getValues(source);

        int i = definition.getParentIndex();
        isBroken = (i >= 0 && family[i].isBroken())
                || lastValues == null
                || !Arrays.deepEquals(lastValues, newValues);

        lastValues = newValues;
    }

    @Override
    public boolean isBroken() {
        return isBroken;
    }

    @Override
    public void markAsBroken() {
        isBroken = true;
        lastValues = null;
    }
}
