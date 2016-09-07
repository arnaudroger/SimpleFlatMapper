package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.util.Supplier;

import java.util.List;

public class ValuedMappingContextFactory<S> implements MappingContextFactory<S> {
    private final Supplier<?>[] suppliers;

    public ValuedMappingContextFactory(List<Supplier<?>> suppliers) {
        this.suppliers = suppliers.toArray(new Supplier[0]);

    }

    @Override
    public MappingContext<S> newContext() {
        return new ValuedMappingContext<S>(getObjects());
    }

    protected Object[] getObjects() {
        Object[] values = new Object[suppliers.length];

        for(int i = 0; i < suppliers.length; i ++) {
            Supplier<?> s = suppliers[i];
            if (s != null) {
                values[i] = s.get();
            }
        }
        return values;
    }
}
