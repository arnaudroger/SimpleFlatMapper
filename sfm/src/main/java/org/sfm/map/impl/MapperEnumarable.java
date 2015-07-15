package org.sfm.map.impl;

import org.sfm.map.Mapper;
import org.sfm.utils.Enumarable;

public class MapperEnumarable<S, T> implements Enumarable<T> {
    private final Enumarable<S> enumarable;
    private final Mapper<S, T> mapper;

    public MapperEnumarable(Mapper<S, T> mapper, Enumarable<S> enumarable) {
        this.mapper = mapper;
        this.enumarable = enumarable;
    }

    @Override
    public boolean next() {
        return enumarable.next();
    }

    @Override
    public T currentValue() {
        return mapper.map(enumarable.currentValue());
    }
}
