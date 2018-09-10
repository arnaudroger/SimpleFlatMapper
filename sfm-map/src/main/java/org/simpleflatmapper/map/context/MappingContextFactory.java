package org.simpleflatmapper.map.context;


import org.simpleflatmapper.converter.ContextFactory;
import org.simpleflatmapper.map.MappingContext;

public interface MappingContextFactory<S> extends ContextFactory {
    MappingContext<S> newContext();
}
