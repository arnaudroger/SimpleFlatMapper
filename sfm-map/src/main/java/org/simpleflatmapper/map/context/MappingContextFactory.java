package org.simpleflatmapper.map.context;


import org.simpleflatmapper.map.MappingContext;

public interface MappingContextFactory<S> {
    MappingContext<S> newContext();
}
