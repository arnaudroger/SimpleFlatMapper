package org.simpleflatmapper.util;

public class ArrayEnumarable<T> implements Enumarable<T> {

    private final T[] objects;
    private int currentIndex = -1;

    public ArrayEnumarable(T[] objects) {
        this.objects = objects;
    }

    @Override
    public boolean next() {
        currentIndex ++;
        if (currentIndex < objects.length) {
            return true;
        }
        return false;
    }

    @Override
    public T currentValue() {
        return objects[currentIndex];
    }
}
