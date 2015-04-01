package org.sfm.map;


public interface MappingContextFactory<S> {
    MappingContext<S> newContext();
}
