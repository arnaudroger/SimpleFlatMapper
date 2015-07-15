package org.sfm.map.impl.context;


import org.sfm.map.MappingContext;

public class ValuedMappingContext<S> extends MappingContext<S> {

    private final Object[] values;

    public ValuedMappingContext(Object[] values) {
        this.values = values;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T context(int i) {
        return (T)values[i];
    }
}
