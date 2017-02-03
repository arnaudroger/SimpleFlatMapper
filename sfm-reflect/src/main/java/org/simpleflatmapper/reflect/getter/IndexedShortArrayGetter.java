package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;

public final class IndexedShortArrayGetter implements Getter<short[], Short>, ShortGetter<short[]> {
    private final int index;

    public IndexedShortArrayGetter(int index) {
        this.index = index;
    }

    @Override
    public short getShort(short[] target) throws Exception {
        return target[index];
    }

    @Override
    public Short get(short[] target) throws Exception {
        return getShort(target);
    }
}
