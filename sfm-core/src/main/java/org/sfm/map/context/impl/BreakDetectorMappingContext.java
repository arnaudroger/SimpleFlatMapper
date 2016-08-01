package org.sfm.map.context.impl;

import org.sfm.map.BreakDetector;
import org.sfm.map.MappingContext;

public class BreakDetectorMappingContext<S> extends MappingContext<S> {

    private final BreakDetector<S>[] breakDetectors;
    private final BreakDetector<S> rootDetector;

    public BreakDetectorMappingContext(BreakDetector<S>[] breakDetectors, int rootDetector) {
        this.breakDetectors = breakDetectors;
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
        return null;
    }
}
