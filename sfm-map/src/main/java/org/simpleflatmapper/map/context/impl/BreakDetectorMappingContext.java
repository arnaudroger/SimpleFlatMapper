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
        this.breakDetectors = toBreakDetectors(keyDefinitions);
        this.rootDetector = breakDetectors[rootKeyDefinition.getIndex()];
    }


    @SuppressWarnings("unchecked")
    private static <S> BreakDetector<S>[] toBreakDetectors(KeyDefinition<S, ?>[] definitions) {
        BreakDetector<S>[] breakDetectors = new BreakDetector[definitions.length];
        for (int i = 0; i < definitions.length; i++) {
            KeyDefinition<S, ?> definition = definitions[i];
            breakDetectors[i] = new BreakDetector<S>(definition);
        }
        return breakDetectors;
    }

    @Override
    public boolean broke(S source) {
        boolean b = rootDetector.broke(source);

        if (b) {
            for(BreakDetector breakDetector : breakDetectors) {
                if (breakDetector != rootDetector) {
                    breakDetector.markRootAsBroken();
                }
            }
        }

        for(BreakDetector<S> breakDetector : breakDetectors) {
            if (breakDetector != rootDetector) {
                breakDetector.handleSource(source);
            }
        }

        return b;
    }

    @Override
    public void handleSource(S source) {
        for(BreakDetector<S> breakDetector : breakDetectors) {
            breakDetector.handleSource(source);
        }
    }

    @Override
    public void markAsBroken() {
        for(BreakDetector breakDetector : breakDetectors) {
            breakDetector.markRootAsBroken();
        }
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

    public BreakDetector<S> getRootDetector() {
        return rootDetector;
    }
}
