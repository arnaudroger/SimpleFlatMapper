package org.sfm.map;


import org.sfm.map.impl.FieldKey;

public abstract class ColumnDefinition<K extends FieldKey<K>> {
    public abstract K rename(K key);
}
