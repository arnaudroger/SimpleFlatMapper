package org.simpleflatmapper.reflect.property;

public class ArrayIndexStartAtProperty {
    public static final ArrayIndexStartAtProperty ONE = new ArrayIndexStartAtProperty(1);
    public static final ArrayIndexStartAtProperty ZERO = new ArrayIndexStartAtProperty(0);
    public final int startIndex;

    public ArrayIndexStartAtProperty(int startIndex) {
        this.startIndex = startIndex;
    }
}
