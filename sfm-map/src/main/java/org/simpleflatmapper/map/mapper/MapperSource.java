package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.reflect.getter.GetterFactory;

public interface MapperSource<S, K extends FieldKey<K>> {
    Class<S> source();
    GetterFactory<S, K> getterFactory();
}
