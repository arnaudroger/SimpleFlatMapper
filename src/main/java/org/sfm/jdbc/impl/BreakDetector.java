package org.sfm.jdbc.impl;


import org.sfm.map.MappingException;

public interface BreakDetector<S> {
    void handle(S source) throws MappingException;
    boolean isBroken();
}
