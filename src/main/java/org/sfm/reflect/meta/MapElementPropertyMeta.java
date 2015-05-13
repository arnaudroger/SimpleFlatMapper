package org.sfm.reflect.meta;

import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

import java.lang.reflect.Type;
import java.util.Map;

public class MapElementPropertyMeta<T extends Map<K, V>, K, V> extends PropertyMeta<T, V> {

	private final ClassMeta<V> valueMetaData;
	private final K key;
	public MapElementPropertyMeta(PropertyNameMatcher propertyNameMatcher, ReflectionService reflectService, ClassMeta<V> valueMetaData, K key) {
		super(propertyNameMatcher.toString(), reflectService);
		this.valueMetaData = valueMetaData;
		this.key = key;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Setter<T, V> newSetter() {
        return new MapSetter<T, K, V>(key);
	}

    @SuppressWarnings("unchecked")
    @Override
    protected Getter<T, V> newGetter() {
        return new MapGetter<T, K, V>(key);
    }

    @Override
	public Type getType() {
		return valueMetaData.getType();
	}


	@Override
	public String getPath() {
		return key + "." + getName();
	}


	private static class MapSetter<T extends  Map<K, V>, K, V> implements Setter<T, V> {
		private final K key;

		private MapSetter(K key) {
			this.key = key;
		}

		@Override
        public void set(T target, V value) throws Exception {
			target.put(key, value);
        }
	}

    private static class MapGetter<T extends  Map<K, V>, K, V> implements Getter<T, V> {
        private final K key;

        private MapGetter(K key) {
            this.key = key;
        }

        @Override
        public V get(T target) throws Exception {
            return target.get(key);
        }
    }

    @Override
    public String toString() {
        return "MapElementPropertyMeta{"
				+"key=" + key +
                '}';
    }
}
