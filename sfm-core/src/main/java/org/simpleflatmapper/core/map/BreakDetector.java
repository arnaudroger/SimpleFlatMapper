package org.simpleflatmapper.core.map;



public interface BreakDetector<S> {
    void handle(S source) throws MappingException;
    boolean isBroken();

    void markAsBroken();
}
