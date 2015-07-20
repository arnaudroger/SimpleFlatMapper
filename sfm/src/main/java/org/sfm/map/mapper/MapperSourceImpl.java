package org.sfm.map.mapper;

import org.sfm.map.FieldKey;
import org.sfm.map.GetterFactory;

public class MapperSourceImpl<S, K extends FieldKey<K>> implements MapperSource<S, K> {

    private final Class<S> source;
    private final GetterFactory<S, K> getterFactory;

    public MapperSourceImpl(Class<S> source, GetterFactory<S, K> getterFactory) {
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

    public MapperSourceImpl<S, K> getterFactory(GetterFactory<S, K> getterFactory) {
        return new MapperSourceImpl<S, K>(source, getterFactory);
    }

}
