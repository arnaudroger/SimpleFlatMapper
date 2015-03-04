package org.sfm.csv.impl;

public class NullBreakDetector implements BreakDetector {
    private boolean brokenCheck;

    @Override
    public boolean isBroken(DelayedCellSetter<?, ?>[] delayedCellSetters) {
        boolean b = !brokenCheck;
        brokenCheck = true;
        return b;
    }

    @Override
    public void reset() {
        brokenCheck = false;
    }
}
