package org.simpleflatmapper.util;

import org.simpleflatmapper.util.Enumarable;

public class ArrayEnumarable<T> implements Enumarable<T> {

    private final T[] objects;
    private int currentIndex = -1;

    public ArrayEnumarable(T[] objects) {
        this.objects = objects;
    }

    @Override
    public boolean next() {
        if (currentIndex < objects.length) {
            currentIndex ++;
            return true;
        }
        return false;
    }

    @Override
    public T currentValue() {
        return objects[currentIndex];
    }
}
