package org.sfm.jdbc;

import org.sfm.jdbc.impl.BreakDetector;

public interface BreakDetectorFactory<T> {
    BreakDetector<T> newInstance();
}
