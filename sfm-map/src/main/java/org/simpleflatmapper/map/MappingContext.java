package org.simpleflatmapper.map;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.context.MappingContextFactory;

public class MappingContext<S> implements Context {

    public static final MappingContext EMPTY_CONTEXT = new MappingContext();

    public static final MappingContextFactory EMPTY_FACTORY = new MappingContextFactory() {
        @Override
        public MappingContext newContext() {
            return EMPTY_CONTEXT;
        }
    };

    public boolean broke(S source) {
        return true;
    }

    public void handleSource(S source) {
    }
    
    public void markAsBroken() {
    }

    @Override
    public <T> T context(int i) {
        return null;
    }

    public void setCurrentValue(int i, Object value) {

    }

    public Object getCurrentValue(int i) {
        return null;
    }

}
