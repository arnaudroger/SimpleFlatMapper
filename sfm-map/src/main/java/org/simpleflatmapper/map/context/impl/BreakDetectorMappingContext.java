package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.KeyDefinition;

public class BreakDetectorMappingContext<S> extends MappingContext<S> {

    private final BreakDetector<S> rootDetector;
    private final MappingContext<S> delegateContext;
    private final BreakDetector<S>[] breakDetectors;

    public BreakDetectorMappingContext(KeyDefinition<S, ?> rootKeyDefinition,
                                       MappingContext<S> delegateContext,
                                       KeyDefinition<S, ?>[] keyDefinitions) {
        this.delegateContext = delegateContext;
        this.breakDetectors = toKeyContext(keyDefinitions);
        this.rootDetector = breakDetectors[rootKeyDefinition.getIndex()];
    }


    @SuppressWarnings("unchecked")
    private static <S> BreakDetector<S>[] toKeyContext(KeyDefinition<S, ?>[] definitions) {
        BreakDetector<S>[] breakDetectors = new BreakDetector[definitions.length];
        for (int i = 0; i < definitions.length; i++) {
            KeyDefinition<S, ?> definition = definitions[i];
            breakDetectors[i] = new BreakDetector<S>(definition, breakDetectors);
        }
        return breakDetectors;
    }

    @Override
    public boolean broke(S source) {
        return rootDetector.broke(source);
    }

    @Override
    public void markAsBroken() {
        rootDetector.markAsBroken();
    }

    @Override
    public <T> T context(int i) {
        return delegateContext.context(i);
    }

    @Override
    public void setCurrentValue(int i,  Object value) {
        this.breakDetectors[i].setValue(value);
    }

    @Override
    public Object getCurrentValue(int i) {
        return breakDetectors[i].getValue();
    }
}
