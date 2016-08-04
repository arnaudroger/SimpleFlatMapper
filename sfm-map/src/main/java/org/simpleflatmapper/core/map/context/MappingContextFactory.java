package org.simpleflatmapper.core.map.context;


import org.simpleflatmapper.core.map.MappingContext;

public interface MappingContextFactory<S> {
    MappingContext<S> newContext();
}
