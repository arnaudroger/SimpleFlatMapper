package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.LongSetter;

public final class IndexedLongArraySetter implements Setter<long[], Long>, LongSetter<long[]> {
    private final int index;

    public IndexedLongArraySetter(int index) {
        this.index = index;
    }

    @Override
    public void setLong(long[] target, long value) throws Exception {
        target[index] = value;
    }

    @Override
    public void set(long[] target, Long value) throws Exception {
        setLong(target, value);
    }
}
