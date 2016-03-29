package org.sfm.map.column;


public class DefaultValueProperty implements ColumnProperty {
    private final Object value;

    public DefaultValueProperty(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
