package org.simpleflatmapper.core.map.context.impl;

import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.core.map.context.MappingContextFactory;
import org.simpleflatmapper.util.Supplier;

import java.util.List;

public class ValuedMapperContextFactory<S> implements MappingContextFactory<S> {
    private final Supplier<?>[] suppliers;

    public ValuedMapperContextFactory(List<Supplier<?>> suppliers) {
        this.suppliers = suppliers.toArray(new Supplier[0]);

    }

    @Override
    public MappingContext<S> newContext() {
        Object[] values = new Object[suppliers.length];

        for(int i = 0; i < suppliers.length; i ++) {
            Supplier<?> s = suppliers[i];
            if (s != null) {
                values[i] = s.get();
            }
        }

        return new ValuedMappingContext<S>(values);
    }
}
