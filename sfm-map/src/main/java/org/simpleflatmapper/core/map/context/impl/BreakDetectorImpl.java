package org.simpleflatmapper.core.map.context.impl;

import org.simpleflatmapper.core.map.BreakDetector;
import org.simpleflatmapper.core.map.MappingException;
import org.simpleflatmapper.core.map.context.KeysDefinition;

import java.util.Arrays;

public class BreakDetectorImpl<S, K> implements BreakDetector<S> {

    private final KeysDefinition<S, K> definition;
    private final BreakDetector<S> parent;
    private Object[] lastValues;

    private boolean isBroken = true;


    public BreakDetectorImpl(KeysDefinition<S, K> definition, BreakDetector<S> parent) {
        this.definition = definition;
        this.parent = parent;
    }

    @Override
    public void handle(S source) throws MappingException {
        if (definition.isEmpty()) {
            return;
        }

        Object[] newValues = definition.getValues(source);

        isBroken = (parent != null && parent.isBroken())
                || lastValues == null
                || !Arrays.equals(lastValues, newValues);

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
