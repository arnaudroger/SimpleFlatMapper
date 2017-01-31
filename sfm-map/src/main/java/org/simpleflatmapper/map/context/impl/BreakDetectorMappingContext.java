package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.BreakDetector;
import org.simpleflatmapper.map.MappingContext;

public class BreakDetectorMappingContext<S> extends MappingContext<S> {

    private final BreakDetector<S>[] originalOrderBreakDetectors;
    private final BreakDetector<S>[] processingDectectors;
    private final BreakDetector<S> rootDetector;
    private final MappingContext<S> delegateContext;
    private final Object[] currentValues;

    public BreakDetectorMappingContext(BreakDetector<S>[] originalOrderBreakDetectors, BreakDetector<S>[] processingDectectors, int rootDetector, MappingContext<S> delegateContext, Object[] currentValues) {
        this.originalOrderBreakDetectors = originalOrderBreakDetectors;
        this.processingDectectors = processingDectectors;
        this.delegateContext = delegateContext;
        this.currentValues = currentValues;
        this.rootDetector = rootDetector == -1 ? null : originalOrderBreakDetectors[rootDetector];
    }

    private BreakDetector<S> getBreakDetector(int i) {
        return originalOrderBreakDetectors != null ? originalOrderBreakDetectors[i] : null;
    }

    @Override
    public boolean broke(int i) {
        BreakDetector<S> breakDetector = getBreakDetector(i);
        return breakDetector == null || breakDetector.isBroken();
    }

    @Override
    public boolean rootBroke() {
        return rootDetector == null || rootDetector.isBroken();
    }

    @Override
    public void handle(S source) {
        if (processingDectectors == null) return;
        for(BreakDetector<S> bs : processingDectectors) {
            if (bs != null) {
                bs.handle(source);
            }
        }
    }

    @Override
    public void markAsBroken() {
        if (processingDectectors == null) return;
        for(BreakDetector<S> bs : processingDectectors) {
            if (bs != null) {
                bs.markAsBroken();
            }
        }
    }

    @Override
    public <T> T context(int i) {
        return delegateContext.context(i);
    }

    @Override
    public void setCurrentValue(int i, Object value) {
        this.currentValues[i] = value;
    }

    @Override
    public Object getCurrentValue(int i) {
        return currentValues[i];
    }
}
