package org.sfm.csv.impl;

public interface BreakDetector {
    public boolean isBroken(DelayedCellSetter<?, ?>[] delayedCellSetters);
    public void reset();
}
