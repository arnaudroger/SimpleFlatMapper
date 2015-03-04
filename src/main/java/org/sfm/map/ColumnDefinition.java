package org.sfm.map;


import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;

public abstract class ColumnDefinition<K extends FieldKey<K>, CD extends  ColumnDefinition<K, CD>> {
    public K rename(K key) {
        return key;
    }

    public boolean hasCustomSource() {
        return false;
    }

    public Type getCustomSourceReturnType() {
        throw new UnsupportedOperationException();
    }

    public boolean ignore() {
        return false;
    }

    public boolean isKey() {
        return false;
    }

    public Predicate<PropertyMeta<?, ?>> keyAppliesTo() {
        return new Predicate<PropertyMeta<?, ?>>() {
            @Override
            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                return false;
            }
        };
    }

    public abstract CD compose(CD columnDefinition);

    public abstract CD addRename(String name);
    public abstract CD addIgnore();

    public abstract CD addKey();
    public abstract CD addKey(Predicate<PropertyMeta<?, ?>> appliesTo);

    protected abstract void appendToStringBuilder(StringBuilder sb);

    public String toString() {
        StringBuilder sb  = new StringBuilder();

        sb.append("ColumnDefinition{");
        appendToStringBuilder(sb);
        sb.append("}");

        return sb.toString();
    }
}
