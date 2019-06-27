package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.getter.NullGetter;

import java.lang.reflect.Type;
import java.util.Map;

public class MapKeyValueElementPropertyMeta<T extends Map<K, V>, K, V> extends PropertyMeta<T, MapKeyValueElementPropertyMeta.KeyValue<K,V>> {

	private final Type propertyType;
	private final MapSetter<T, K, V> setter;

	public MapKeyValueElementPropertyMeta(Type ownerType,
										  ReflectionService reflectService,
										  Type propertyType) {
		super("entry", ownerType, reflectService);
		this.propertyType = propertyType;
		setter = new MapSetter<T, K, V>();
	}

	@Override
	public Setter<T, MapKeyValueElementPropertyMeta.KeyValue<K,V>> getSetter() {
        return setter;
	}

    @Override
    public Getter<T, MapKeyValueElementPropertyMeta.KeyValue<K,V>> getGetter() {
        return NullGetter.getter();
    }

    @Override
	public Type getPropertyType() {
		return propertyType;
	}


	@Override
	public String getPath() {
		return getName();
	}

	@Override
	public PropertyMeta<T, KeyValue<K, V>> withReflectionService(ReflectionService reflectionService) {
		return new MapKeyValueElementPropertyMeta<T, K, V>(getOwnerType(), reflectionService, propertyType);
	}

	@Override
	public PropertyMeta<T, KeyValue<K, V>> toNonMapped() {
		throw new UnsupportedOperationException();
	}


	private static class MapSetter<T extends  Map<K, V>, K, V> implements Setter<T, KeyValue<K, V>> {
		@Override
		public void set(T target, KeyValue<K, V> value) throws Exception {
			target.put(value.getKey(), value.getValue());
		}
	}


    @Override
    public String toString() {
        return "MapKeyValueElementPropertyMeta{}";
    }

    public static class KeyValue<K, V> {

		private final K key;
		private final V value;
		
		public KeyValue(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}
	}
}
