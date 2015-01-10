package org.sfm.map;


import org.sfm.map.impl.FieldKey;

public abstract class ColumnDefinition<K extends FieldKey<K>> {
    public abstract K rename(K key);

    public static <K extends FieldKey<K>> ColumnDefinition<K> identity() {
        return new ColumnDefinition<K>() {
            @Override
            public K rename(K key) {
                return key;
            }
        };
    }
}
