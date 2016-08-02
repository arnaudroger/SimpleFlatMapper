package org.simpleflatmapper.core.map;

import org.simpleflatmapper.core.map.mapper.MapperKey;

public interface IMapperCache<K extends FieldKey<K>, M> {
    @SuppressWarnings("unchecked")
    void add(MapperKey<K> key, M mapper);

    M get(MapperKey<K> key);

    int size();
}
