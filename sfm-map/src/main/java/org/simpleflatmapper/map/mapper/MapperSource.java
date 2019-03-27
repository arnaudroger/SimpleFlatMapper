package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;

public interface MapperSource<S, K extends FieldKey<K>> {
    Class<S> source();
    ContextualGetterFactory<? super S, K> getterFactory();
}
