package org.sfm.map;


import java.lang.reflect.Type;

public abstract class ColumnDefinition<K extends FieldKey<K>, CD extends  ColumnDefinition<K, CD>> {
    public abstract K rename(K key);

    public abstract boolean hasCustomSource();

    public abstract Type getCustomSourceReturnType();

    public abstract boolean ignore();

    public abstract CD compose(CD columnDefinition);

    public abstract CD addRename(String name);

    public abstract CD addIgnore();

    protected abstract void appendToStringBuilder(StringBuilder sb);

    public String toString() {
        StringBuilder sb  = new StringBuilder();

        sb.append("ColumnDefinition{");
        appendToStringBuilder(sb);
        sb.append("}");

        return sb.toString();
    }
}
