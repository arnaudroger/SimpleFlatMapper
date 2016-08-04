package org.simpleflatmapper.core.map.mapper;


import org.simpleflatmapper.core.map.FieldKey;
import org.simpleflatmapper.core.reflect.getter.GetterFactory;

public interface MapperSource<S, K extends FieldKey<K>> {
    Class<S> source();
    GetterFactory<S, K> getterFactory();
}
