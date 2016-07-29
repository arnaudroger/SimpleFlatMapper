package org.sfm.map.context;


import org.sfm.map.MappingContext;

public interface MappingContextFactory<S> {
    MappingContext<S> newContext();
}
