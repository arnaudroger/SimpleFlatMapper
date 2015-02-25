package org.sfm.map;


public interface MappingContextFactory<S> {
    public MappingContext<S> newContext();
}
