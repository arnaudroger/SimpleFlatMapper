package org.simpleflatmapper.map;

import org.simpleflatmapper.map.mapper.MapperKey;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class CHMMapperCache<K extends FieldKey<K>, M> implements IMapperCache<K, M> {

	@SuppressWarnings("unchecked")
	private final ConcurrentMap<MapperKey<K>, M> map = new ConcurrentHashMap<>();
	
	@SuppressWarnings("unchecked")
	public void add(final MapperKey<K> key, final M mapper) {
		map.putIfAbsent(key, mapper);
	}

	public M get(MapperKey<K> key) {
		return map.get(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
    public String toString() {
        return "CHMMapperCache{" + map +
                '}';
    }
}
