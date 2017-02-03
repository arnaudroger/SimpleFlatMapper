package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.LongGetter;

public final class IndexedLongArrayGetter implements Getter<long[], Long>, LongGetter<long[]> {
    private final int index;

    public IndexedLongArrayGetter(int index) {
        this.index = index;
    }

    @Override
    public long getLong(long[] target) throws Exception {
        return target[index];
    }

    @Override
    public Long get(long[] target) throws Exception {
        return getLong(target);
    }
}
