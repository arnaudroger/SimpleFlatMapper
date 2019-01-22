package org.simpleflatmapper.util;

import java.util.ArrayList;

public final class ArrayListEnumerable<T> implements Enumerable<T> {

    private final ArrayList<T> objects;
    private int currentIndex = -1;

    public ArrayListEnumerable(ArrayList<T> objects) {
        this.objects = objects;
    }

    @Override
    public boolean next() {
        currentIndex ++;
        if (currentIndex < objects.size()) {
            return true;
        }
        return false;
    }

    @Override
    public T currentValue() {
        return objects.get(currentIndex);
    }
}
