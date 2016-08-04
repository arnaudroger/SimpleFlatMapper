package org.simpleflatmapper.core.map.context;

public interface KeySourceGetter<K, S> {
    Object getValue(K key, S source) throws Exception;
}
