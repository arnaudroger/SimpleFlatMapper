package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;

public final class IndexedShortArraySetter implements Setter<short[], Short>, ShortSetter<short[]> {
    private final int index;

    public IndexedShortArraySetter(int index) {
        this.index = index;
    }

    @Override
    public void setShort(short[] target, short value) throws Exception {
        target[index] = value;
    }

    @Override
    public void set(short[] target, Short value) throws Exception {
        setShort(target, value);
    }
}
