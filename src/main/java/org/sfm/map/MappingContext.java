package org.sfm.map;

import org.sfm.jdbc.impl.BreakDetector;

public class MappingContext<S> {

    private final BreakDetector<S>[] breakDetectors;
    private final BreakDetector<S> rootDetector;

    public MappingContext(BreakDetector<S>[] breakDetectors, BreakDetector<S> rootDetector) {
        this.breakDetectors = breakDetectors;
        this.rootDetector = rootDetector;
    }

    public BreakDetector<S> getBreakDetector(int i) {
        return breakDetectors[i];
    }

    public boolean broke(int i) {
        return getBreakDetector(i).isBroken();
    }

    public boolean rootBroke() {
        return rootDetector == null || rootDetector.isBroken();
    }

    public void handle(S source) {
        for(BreakDetector<S> bs : breakDetectors) {
            if (bs != null) {
                bs.handle(source);
            }
        }
    }

    public void markAsBroken() {
        for(BreakDetector<S> bs : breakDetectors) {
            if (bs != null) {
                bs.markAsBroken();
            }
        }
    }


}
