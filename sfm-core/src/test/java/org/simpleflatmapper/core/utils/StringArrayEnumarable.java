package org.simpleflatmapper.core.utils;

public class StringArrayEnumarable implements Enumarable<String> {
    final String[] values;
    int currentIndex = -1;

    public StringArrayEnumarable(String[] values) {
        this.values = values;
    }

    @Override
    public boolean next() {
        currentIndex++;
        return currentIndex < values.length;
    }

    @Override
    public String currentValue() {
        return values[currentIndex];
    }
}
