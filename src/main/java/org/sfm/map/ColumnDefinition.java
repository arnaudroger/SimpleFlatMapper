package org.sfm.map;


import java.lang.reflect.Type;

public abstract class ColumnDefinition<K extends FieldKey<K>, CD extends  ColumnDefinition<K, CD>> {
    public abstract K rename(K key);

    public abstract boolean hasCustomSource();

    public abstract Type getCustomSourceReturnType();

    public abstract boolean ignore();

    public abstract CD addRename(String name);

    public abstract CD addIgnore();

}
