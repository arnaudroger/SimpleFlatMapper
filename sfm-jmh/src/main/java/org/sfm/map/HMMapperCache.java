package org.sfm.map;

import org.sfm.map.mapper.MapperKey;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class HMMapperCache<K extends FieldKey<K>, M> {

	@SuppressWarnings("unchecked")
	private final Map<MapperKey<K>, M> map = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public void add(final MapperKey<K> key, final M mapper) {
		map.putIfAbsent(key, mapper);
	}

	public M get(MapperKey<K> key) {
		return map.get(key);
	}

    @Override
    public String toString() {
        return "CHMMapperCache{" + map +
                '}';
    }
}
