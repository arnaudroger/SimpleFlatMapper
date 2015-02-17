package org.sfm.jdbc.impl;


import org.sfm.map.MappingException;

public interface BreakDetector<S> {
    boolean isBreaking(S source) throws MappingException;
}
