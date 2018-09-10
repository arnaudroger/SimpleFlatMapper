package org.simpleflatmapper.converter;

public class EmptyContextFactory implements ContextFactory {
    
    private EmptyContextFactory(){
    }
    
    @Override
    public Context newContext() {
        return EmptyContext.INSTANCE;
    }

    public static final EmptyContextFactory INSTANCE = new EmptyContextFactory();
}
