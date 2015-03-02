package org.sfm.map;

import org.sfm.jdbc.impl.BreakDetector;

public class MappingContext<S> {

    private final BreakDetector<S>[] breakDetectors;

    public MappingContext(BreakDetector<S>[] breakDetectors) {
        this.breakDetectors = breakDetectors;
    }

    public BreakDetector<S> getBreakDetector(int i) {
        return breakDetectors[i];
    }

    public boolean broke(int i) {
        return getBreakDetector(i).isBroken();
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

    public boolean rootBroke() {
        for(int i = 0; i < breakDetectors.length; i++) {
            if (breakDetectors[i] != null) {
                return breakDetectors[i].isBroken();
            }
        }
        return true;
    }
}
