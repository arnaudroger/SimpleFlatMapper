package org.sfm.map.mapper;


import org.sfm.map.FieldKey;
import org.sfm.map.GetterFactory;

public interface MapperSource<S, K extends FieldKey<K>> {
    Class<S> source();
    GetterFactory<S, K> getterFactory();
}
