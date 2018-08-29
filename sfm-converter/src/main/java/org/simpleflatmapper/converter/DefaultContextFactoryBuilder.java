package org.simpleflatmapper.converter;

import org.simpleflatmapper.util.Supplier;

import java.util.ArrayList;

public final class DefaultContextFactoryBuilder implements ContextFactoryBuilder {
    
    
    private final ArrayList<Supplier<?>> suppliers = new ArrayList<Supplier<?>>();
    
    public DefaultContextFactoryBuilder(){
    }
    
    @Override
    public int addSupplier(Supplier<?> supplier) {
        int index = suppliers.size();
        suppliers.add(supplier);
        return index;
    }

    @Override
    public ContextFactory build() {
        return suppliers.isEmpty() ? EmptyContextFactory.INSTANCE : new DefaultContextFactory(suppliers.toArray(new Supplier[0]));
    }
}
