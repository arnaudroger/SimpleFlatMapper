package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.context.Key;

import java.util.Arrays;

public final class KeyObjectStore {

    private static final int DEFAULT_SIZE = 16;
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private Key[] keys;
    private Object[] values;
    private int size;
    private int nbValues;
    private int resizeThreshold;
    private int mask;

    public KeyObjectStore() {
        nbValues = 0;
        initSize(DEFAULT_SIZE);
    }

    private int calculateMask(int size) {
        return size - 1;
    }

    public void put(Key key, Object value) {
        checkCapacity();

        int index = getIndex(key);

        if (index >= 0) {
            throw new IllegalArgumentException("Already has key " + key);
        }

        setValue(key, value, index + size);
    }

    private void checkCapacity() {
        if (nbValues > resizeThreshold) resize();
    }


    public Object get(Key key) {
        int index = getIndex(key);

        if (index >= 0) {
            return getValue(index);
        }
        return null;
    }

    private void setValue(Key key, Object value, int index) {
        keys[index] = key;
        values[index] = value;
        nbValues++;
    }

    private Object getValue(int index) {
        return values[index];
    }

    private int getIndex(Key key) {
        int hashCode = key.hashCode();

        int startIndex = toIndex(hashCode);

        for(int i = startIndex; i < size; i++) {
            Key k = keys[i];
            if (k == null)
                return i - size;
            if (hashCode == k.hashCode() && key.equals(k)) {
                return i;
            }
        }

        for(int i = 0; i < startIndex; i++) {
            Key k = keys[i];
            if (k == null)
                return i - size;
            if (hashCode == k.hashCode() && key.equals(k)) {
                return i;
            }
        }

        throw new IllegalStateException("Could not find the key or a free sport...");
    }

    private void resize() {
        if (size < MAXIMUM_CAPACITY) {
            Key[] oldKeys = keys;
            Object[] oldValues = values;
            int oldSize = size;

            initSize(size << 1);

            for (int i = 0; i < oldSize; i++) {
                Key k = oldKeys[i];
                if (k != null) {
                    int ki = freeSpot(k);
                    keys[ki] = k;
                    values[ki] = oldValues[i];
                }
            }
        }
    }

    private int freeSpot(Key key) {
        int hashCode = key.hashCode();

        int startIndex = toIndex(hashCode);

        for(int i = startIndex; i < size; i++) {
            Key k = keys[i];
            if (k == null)
                return i;
        }

        for(int i = 0; i < startIndex; i++) {
            Key k = keys[i];
            if (k == null)
                return i;
        }

        throw new IllegalStateException("Could not find the key or a free sport...");
    }

    private void initSize(int newSize) {
        keys = new Key[newSize];
        values = new Object[newSize];
        mask = calculateMask(newSize);
        size = newSize;
        resizeThreshold = size >> 1;
    }

    private int toIndex(int hashCode) {
        return (hashCode & mask);
    }


    public void clear() {
        Arrays.fill(keys, null);
        Arrays.fill(values, null);
        nbValues = 0;
    }
}
