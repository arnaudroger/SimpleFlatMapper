package org.sfm.map.impl;

import org.sfm.map.FieldKey;
import org.sfm.map.GetterFactory;

public class FieldMapperSourceImpl<S, K extends FieldKey<K>> implements FieldMapperSource<S, K>{

    private final Class<S> source;
    private final GetterFactory<S, K> getterFactory;

    public FieldMapperSourceImpl(Class<S> source, GetterFactory<S, K> getterFactory) {
        this.source = source;
        this.getterFactory = getterFactory;
    }

    @Override
    public Class<S> source() {
        return source;
    }

    @Override
    public GetterFactory<S, K> getterFactory() {
        return getterFactory;
    }

    public FieldMapperSourceImpl<S, K> getterFactory(GetterFactory<S, K> getterFactory) {
        return new FieldMapperSourceImpl<S, K>(source, getterFactory);
    }

}
