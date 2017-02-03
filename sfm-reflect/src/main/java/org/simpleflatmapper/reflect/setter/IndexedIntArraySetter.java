package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.IntSetter;

public final class IndexedIntArraySetter implements Setter<int[], Integer>, IntSetter<int[]> {
    private final int index;

    public IndexedIntArraySetter(int index) {
        this.index = index;
    }

    @Override
    public void setInt(int[] target, int value) throws Exception {
        target[index] = value;
    }

    @Override
    public void set(int[] target, Integer value) throws Exception {
        setInt(target, value);
    }
}
