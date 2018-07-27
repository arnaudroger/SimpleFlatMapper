package org.simpleflatmapper.util;

public class ArrayEnumerable<T> implements Enumerable<T> {

    private final T[] objects;
    private int currentIndex = -1;

    public ArrayEnumerable(T[] objects) {
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
