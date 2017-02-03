package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.IntGetter;

public final class IndexedIntArrayGetter implements Getter<int[], Integer>, IntGetter<int[]> {
    private final int index;

    public IndexedIntArrayGetter(int index) {
        this.index = index;
    }

    @Override
    public int getInt(int[] target) throws Exception {
        return target[index];
    }

    @Override
    public Integer get(int[] target) throws Exception {
        return getInt(target);
    }
}
