package org.sfm.map;

import org.sfm.jdbc.impl.BreakDetector;

public class MappingContext<S> {

    private final BreakDetector<S>[] breakDetectors;
    private final BreakDetector<S> rootDetector;

    public MappingContext(BreakDetector<S>[] breakDetectors, int rootDetector) {
        this.breakDetectors = breakDetectors;
        this.rootDetector = rootDetector == -1 ? null : breakDetectors[rootDetector];
    }

    public BreakDetector<S> getBreakDetector(int i) {
        return breakDetectors != null ? breakDetectors[i] : null;
    }

    public boolean broke(int i) {
        BreakDetector<S> breakDetector = getBreakDetector(i);
        return breakDetector == null || breakDetector.isBroken();
    }

    public boolean rootBroke() {
        return rootDetector == null || rootDetector.isBroken();
    }

    public void handle(S source) {
        if (breakDetectors == null) return;
        for(BreakDetector<S> bs : breakDetectors) {
            if (bs != null) {
                bs.handle(source);
            }
        }
    }

    public void markAsBroken() {
        if (breakDetectors == null) return;
        for(BreakDetector<S> bs : breakDetectors) {
            if (bs != null) {
                bs.markAsBroken();
            }
        }
    }


}
