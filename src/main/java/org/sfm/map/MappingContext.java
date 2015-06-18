package org.sfm.map;

public class MappingContext<S> {

    public static final MappingContext INSTANCE = new MappingContext();

    public static final MappingContextFactory EMPTY_FACTORY = new MappingContextFactory() {
        @Override
        public MappingContext newContext() {
            return INSTANCE;
        }
    };

    public boolean broke(int i) {
        return true;
    }

    public boolean rootBroke() {
        return true;
    }

    public void markAsBroken() {
    }

    public <T> T context(int i) {
        return null;
    }

    public void handle(S source) {
    }
}
