package org.sfm.map;


import org.sfm.map.impl.FieldKey;

public interface ColumnDefinition<K extends FieldKey<K>> {
    public K rename(K key);


    public static <K extends FieldKey<K>> ColumnDefinition<K> identity() {
        return new ColumnDefinition<K>() {
            @Override
            public K rename(K key) {
                return key;
            }
        };
    }
}
