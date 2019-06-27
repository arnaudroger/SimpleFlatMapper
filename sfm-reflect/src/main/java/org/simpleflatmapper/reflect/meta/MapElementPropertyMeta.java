package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;

import java.lang.reflect.Type;
import java.util.Map;

public class MapElementPropertyMeta<T extends Map<K, V>, K, V> extends PropertyMeta<T, V> {

	private final ClassMeta<V> valueMetaData;
	private final K key;
	private final MapSetter<T, K, V> setter;
	private final MapGetter<T, K, V> getter;


	public MapElementPropertyMeta(PropertyNameMatcher propertyNameMatcher, Type ownerType, ReflectionService reflectService, ClassMeta<V> valueMetaData, K key) {
		super(propertyNameMatcher.toString(), ownerType, reflectService);
		this.valueMetaData = valueMetaData;
		this.key = key;
		setter = new MapSetter<T, K, V>(key);
		getter = new MapGetter<T, K, V>(key);
	}

	public MapElementPropertyMeta(String name, Type ownerType, ReflectionService reflectService, ClassMeta<V> valueMetaData, K key, MapSetter<T, K, V> setter, MapGetter<T, K, V> getter) {
		super(name, ownerType, reflectService);
		this.valueMetaData = valueMetaData;
		this.key = key;
		this.setter = setter;
		this.getter = getter;
	}

	@Override
	public Setter<T, V> getSetter() {
        return setter;
	}

    @Override
    public Getter<T, V> getGetter() {
        return getter;
    }

    @Override
	public Type getPropertyType() {
		return valueMetaData.getType();
	}


	@Override
	public String getPath() {
		return key + "." + getName();
	}

	@Override
	public PropertyMeta<T, V> withReflectionService(ReflectionService reflectionService) {
		return new MapElementPropertyMeta<T, K, V>(getName(), getOwnerType(), reflectionService, reflectionService.<V>getClassMeta(valueMetaData.getType()), key, setter, getter);
	}

	@Override
	public PropertyMeta<T, V> toNonMapped() {
		throw new UnsupportedOperationException();
	}

	public K getKey() {
		return key;
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
