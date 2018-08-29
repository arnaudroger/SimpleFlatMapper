package org.simpleflatmapper.converter;

import org.simpleflatmapper.util.Supplier;

public final class EmptyContextFactoryBuilder implements ContextFactoryBuilder {
    
    public static final EmptyContextFactoryBuilder INSTANCE = new EmptyContextFactoryBuilder();
    
    private EmptyContextFactoryBuilder(){
    }
    @Override
    public int addSupplier(Supplier<?> supplier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContextFactory build() {
        return EmptyContextFactory.INSTANCE;
    }
}
