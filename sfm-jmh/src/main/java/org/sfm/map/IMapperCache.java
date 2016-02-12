package org.sfm.map;

import org.sfm.map.mapper.MapperKey;

/**
 * Created by aroger on 12/02/2016.
 */
public interface IMapperCache<K extends FieldKey<K>, M> {
    @SuppressWarnings("unchecked")
    void add(MapperKey<K> key, M mapper);

    M get(MapperKey<K> key);

    int size();
}
