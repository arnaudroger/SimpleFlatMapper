package org.simpleflatmapper.converter;

import org.simpleflatmapper.util.Supplier;

public class DefaultContextFactory implements ContextFactory {
    
    private final Supplier<?>[] suppliers;

    public DefaultContextFactory(Supplier<?>[] suppliers) {
        this.suppliers = suppliers;
    }

    @Override
    public Context newContext() {
        Object[] resources = new Object[suppliers.length];
        
        for(int i = 0; i < resources.length; i++) {
            resources[i] = suppliers[i].get();
        }
        return new DefaultContext(resources);
    }
}
