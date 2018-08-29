package org.simpleflatmapper.converter;

public class EmptyContext implements Context {
    public static final EmptyContext INSTANCE = new EmptyContext();
    private EmptyContext() {}
    @Override
    public <T> T context(int i) {
        throw new UnsupportedOperationException();
    }
}
