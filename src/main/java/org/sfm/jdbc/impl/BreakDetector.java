package org.sfm.jdbc.impl;

public interface BreakDetector<S> {
    boolean isBreaking(S source);
}
