package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.BreakDetector;
import org.simpleflatmapper.map.MappingContext;

public class BreakDetectorMappingContext<S> extends MappingContext<S> {

    private final BreakDetector<S>[] breakDetectors;
    private final BreakDetector<S> rootDetector;
    private final MappingContext<S> delegateContext;

    public BreakDetectorMappingContext(BreakDetector<S>[] breakDetectors, int rootDetector, MappingContext<S> delegateContext) {
        this.breakDetectors = breakDetectors;
        this.delegateContext = delegateContext;
        this.rootDetector = rootDetector == -1 ? null : breakDetectors[rootDetector];
    }

    private BreakDetector<S> getBreakDetector(int i) {
        return breakDetectors != null ? breakDetectors[i] : null;
    }

    @Override public boolean broke(int i) {
        BreakDetector<S> breakDetector = getBreakDetector(i);
        return breakDetector == null || breakDetector.isBroken();
    }

    @Override public boolean rootBroke() {
        return rootDetector == null || rootDetector.isBroken();
    }

    @Override public void handle(S source) {
        if (breakDetectors == null) return;
        for(BreakDetector<S> bs : breakDetectors) {
            if (bs != null) {
                bs.handle(source);
            }
        }
    }

    @Override public void markAsBroken() {
        if (breakDetectors == null) return;
        for(BreakDetector<S> bs : breakDetectors) {
            if (bs != null) {
                bs.markAsBroken();
            }
        }
    }

    @Override
    public <T> T context(int i) {
        return delegateContext.context(i);
    }
}
