package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.reflect.getter.GetterFactory;

public class MapperSourceImpl<S, K extends FieldKey<K>> implements MapperSource<S, K> {

    private final Class<S> source;
    private final ContextualGetterFactory<S, K> getterFactory;

    public MapperSourceImpl(Class<S> source, ContextualGetterFactory<S, K> getterFactory) {
        this.source = source;
        this.getterFactory = getterFactory;
    }

    @Override
    public Class<S> source() {
        return source;
    }

    @Override
    public ContextualGetterFactory<S, K> getterFactory() {
        return getterFactory;
    }

    public MapperSourceImpl<S, K> getterFactory(ContextualGetterFactory<S, K> getterFactory) {
        return new MapperSourceImpl<S, K>(source, getterFactory);
    }

    public MapperSourceImpl<S, K> getterFactory(GetterFactory<S, K> getterFactory) {
        return new MapperSourceImpl<S, K>(source, new ContextualGetterFactoryAdapter<S, K>(getterFactory));
    }

}
