package org.sfm.map;

public interface MappingContext<S> {
    boolean broke(int i);
    boolean rootBroke();
    void markAsBroken();

    void handle(S source);
}
