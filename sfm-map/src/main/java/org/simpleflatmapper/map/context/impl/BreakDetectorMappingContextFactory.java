package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.KeyDefinition;
import org.simpleflatmapper.map.context.MappingContextFactory;

public class BreakDetectorMappingContextFactory<S> implements MappingContextFactory<S> {
    private final KeyDefinition<S, ?> rootKeyDefinition;
    private final KeyDefinition<S, ?>[] keyDefinitions;
    private final MappingContextFactory<S> delegateFactory;

    public BreakDetectorMappingContextFactory(
                                        KeyDefinition<S, ?> rootKeyDefinition,
                                        KeyDefinition<S, ?>[] keyDefinitions,
                                        MappingContextFactory<S> delegateFactory) {
        this.rootKeyDefinition = rootKeyDefinition;
        this.keyDefinitions = keyDefinitions;
        this.delegateFactory = delegateFactory;
    }

    @Override
    public MappingContext<S> newContext() {
        return new BreakDetectorMappingContext<S>(rootKeyDefinition, delegateFactory.newContext(), keyDefinitions);
    }


}
