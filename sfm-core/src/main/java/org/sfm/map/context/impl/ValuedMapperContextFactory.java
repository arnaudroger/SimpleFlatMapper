package org.sfm.map.context.impl;

import org.sfm.map.MappingContext;
import org.sfm.map.context.MappingContextFactory;
import org.sfm.utils.Supplier;

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
